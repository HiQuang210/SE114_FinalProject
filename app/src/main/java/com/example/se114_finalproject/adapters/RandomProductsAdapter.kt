package com.example.se114_finalproject.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.se114_finalproject.data.Product
import com.example.se114_finalproject.databinding.RandomRvItemBinding
import com.example.se114_finalproject.helper.getProductPrice

class RandomProductsAdapter : RecyclerView.Adapter<RandomProductsAdapter.RandomProductsViewHolder>(){

    inner class RandomProductsViewHolder(private val binding: RandomRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgRandomRvItem)
                tvRandomProductName.text = product.name
                val priceAfterOffer = product.discountPercentage.getProductPrice(product.price)
                tvNewPrice.text = "${priceAfterOffer.toInt()} VND"
                tvOldPrice.text = "${product.price.toInt()}"
                tvOldPrice.paintFlags = tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                if (product.discountPercentage == null)
                {
                    tvOldPrice.visibility = View.INVISIBLE
                    tvNewPrice.text = "${product.price.toInt()} VND"
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomProductsViewHolder {
        return RandomProductsViewHolder(
            RandomRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RandomProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Product) -> Unit)? = null
}