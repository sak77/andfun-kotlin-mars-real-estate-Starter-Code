/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsProperty

class PhotoGridAdapter(val marsPropertyClickListener: MarsPropertyClickListener) : ListAdapter<MarsProperty, PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {

    //Define a class that takes a lambda as input. Functional programming...i guess
    class MarsPropertyClickListener(val clickListener : (property : MarsProperty) -> Unit) {
        fun onClick(marsProperty: MarsProperty){
            clickListener(marsProperty)
        }
    }

    //ViewHolder class
    class MarsPropertyViewHolder (val gridViewItemBinding: GridViewItemBinding, private val clickListener: MarsPropertyClickListener) : RecyclerView.ViewHolder(gridViewItemBinding.root) {

        //Bind image data
        fun bindData(marsProperty: MarsProperty) {
            //gridViewItemBinding.marsImage.setImageURI(Uri.parse(marsProperty.imgSrcUrl))
            gridViewItemBinding.property = marsProperty
            gridViewItemBinding.clicklistener = clickListener
            gridViewItemBinding.executePendingBindings()    //This is necessary in recyclerview adapter...
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarsPropertyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val gridViewItemBinding = GridViewItemBinding.inflate(layoutInflater)
        return MarsPropertyViewHolder(gridViewItemBinding, marsPropertyClickListener)
    }

    override fun onBindViewHolder(holder: MarsPropertyViewHolder, position: Int) {
        val currentProperty = getItem(position)
        holder.bindData(currentProperty)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
