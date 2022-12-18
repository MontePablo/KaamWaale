package com.example.kaamwaale.models

import com.example.kaamwaale.daos.CategoryDao

class Category() {
    var name:String =""
    var pic:String=""
    var subCategories=ArrayList<Category>()
}