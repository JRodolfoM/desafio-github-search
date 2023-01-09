package br.com.jrmantovani.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.jrmantovani.githubsearch.databinding.RepositoryItemBinding
import br.com.jrmantovani.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var repoItemLister: (Repository) -> Unit = {}
    var btnShareLister: (Repository) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RepositoryItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = repositories[position]
        holder.bind(repository)

        holder.itemView.setOnClickListener {
         repoItemLister(repository)
        }

        holder.shareLister(repository)

    }


    override fun getItemCount(): Int {
        return repositories.size
    }

   inner class ViewHolder(repositoryItemBinding: RepositoryItemBinding) :
        RecyclerView.ViewHolder(repositoryItemBinding.root) {
       private val binding: RepositoryItemBinding


        init {
            binding = repositoryItemBinding
        }

       fun bind(repository: Repository){
           binding.tvNome.text = repository.name

       }

       fun shareLister( repository: Repository){
           binding.ivShare.setOnClickListener{
               btnShareLister(repository)
           }
       }



    }

}
