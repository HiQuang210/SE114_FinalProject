package com.example.se114_finalproject.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.se114_finalproject.data.order.Order
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.se114_finalproject.R
import com.example.se114_finalproject.data.order.OrderStatus
import com.example.se114_finalproject.data.order.getOrderStatus
import com.example.se114_finalproject.databinding.OrderItemBinding

class AllOrdersAdapter : Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    inner class OrdersViewHolder(private val binding: OrderItemBinding) : ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date
                val context = itemView.context

                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> {
                        ColorDrawable(ContextCompat.getColor(context, R.color.orange_yellow))
                    }
                    is OrderStatus.Confirmed -> {
                        ColorDrawable(ContextCompat.getColor(context, R.color.green))
                    }
                    is OrderStatus.Delivered -> {
                        ColorDrawable(ContextCompat.getColor(context, R.color.green))
                    }
                    is OrderStatus.Shipped -> {
                        ColorDrawable(ContextCompat.getColor(context, R.color.green))
                    }
                    is OrderStatus.Canceled -> {
                        ColorDrawable(ContextCompat.getColor(context, R.color.red))
                    }
                    is OrderStatus.Returned -> {
                        ColorDrawable(ContextCompat.getColor(context, R.color.red))
                    }
                }

                imageOrderState.setImageDrawable(colorDrawable)
            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Order) -> Unit)? = null
}
