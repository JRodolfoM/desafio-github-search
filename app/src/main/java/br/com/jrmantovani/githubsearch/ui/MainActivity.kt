package br.com.jrmantovani.githubsearch.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.jrmantovani.githubsearch.R
import br.com.jrmantovani.githubsearch.data.GitHubService
import br.com.jrmantovani.githubsearch.data.RetrofitService
import br.com.jrmantovani.githubsearch.databinding.ActivityMainBinding
import br.com.jrmantovani.githubsearch.domain.Repository
import br.com.jrmantovani.githubsearch.ui.adapter.RepositoryAdapter
import kotlinx.coroutines.*
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val TAG = "info_github"
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var job: Job? = null
    private var adapter: RepositoryAdapter? = null
    private val gitHubAPI by lazy {
        RetrofitService.getAPI(GitHubService::class.java)
    }

    private var alerta: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showUserName()
        setupListeners()



    }

    private fun modalCarremento(){
        val li = layoutInflater
        val view: View = li.inflate(R.layout.modal_carregamento, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        builder.setCancelable(false)
        alerta = builder.create()
        alerta?.getWindow()?.setBackgroundDrawableResource(R.drawable.shape_modal)
        alerta?.show()


    }

    private fun setupListeners() {

        binding.btnConfirmar.setOnClickListener {
            modalCarremento()
            saveUserLocal()
            getAllReposByUserName()
        }


    }


  
    private fun saveUserLocal() {
        val preferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = preferences.edit()
        editor.putString("nome", binding.etNomeUsuario.text.toString())
        editor.commit()

    }

    private fun showUserName() {
        val preferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val user = preferences.getString("nome", "")
        if (!user.equals("")) {
            binding.etNomeUsuario.setText(user)
            modalCarremento()
            getAllReposByUserName()
        }
    }

    fun getAllReposByUserName() {
        job = CoroutineScope(Dispatchers.IO).launch {

            var resposta: Response<List<Repository>>? = null

            try {

                resposta = gitHubAPI.getAllRepositoriesByUser(binding.etNomeUsuario.text.toString())

            } catch (e: Exception) {
                Log.i(TAG, "erro ${e.message}")
                e.printStackTrace()

            }

            if (resposta != null) {
                if (resposta.isSuccessful) {

                    val listaDados = resposta.body()

                    if (listaDados != null) {
                        withContext(Dispatchers.Main) {
                            setupAdapter(listaDados)
                            alerta?.dismiss()
                        }

                    }


                } else {
                    withContext(Dispatchers.Main) {
                        alerta?.dismiss()
                        Toast.makeText(applicationContext, "Erro na busca", Toast.LENGTH_SHORT).show()
                    }

                    Log.i(TAG, "Erro codigo status: ${resposta.code()}")
                }
            } else {
                withContext(Dispatchers.Main) {
                    alerta?.dismiss()
                    Toast.makeText(applicationContext, "Erro na busca", Toast.LENGTH_SHORT).show()
                }
                Log.i(TAG, "Resposta nula")
            }


        }

    }

    fun setupAdapter(list: List<Repository>) {
        adapter = RepositoryAdapter(list)
        binding.rvListaRepositories.adapter = adapter
        binding.rvListaRepositories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapter?.repoItemLister = {repository -> openBrowser(repository.htmlUrl)}
        adapter?.btnShareLister = {repository -> shareRepositoryLink(repository.htmlUrl) }

    }

    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

    override fun onStop() {
        super.onStop()

        job?.cancel()

    }


}