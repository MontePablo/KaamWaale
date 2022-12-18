package com.example.kaamwaale.models

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Gig {
    var title=""
    var category=ArrayList<String>()
    var packages=ArrayList<Package>()
    var description=""
    var images=ArrayList<String>()
    var serviceType=""
    var lat:Double=0.0
    var lng:Double=0.0
    var hash=""
    var address=""
    var price="10"
    var saveList=ArrayList<String>()
    var ratings=ArrayList<HashMap<String, Objects>>()
    //    map["name"] map["date"] map["commment"] map["image"] map["rating"]
    var ratingCount="1"
    var avgRating="3.5"


    var name=""
    var profession=""
    var uid=""
    var userRatingCount="1"
    var userAvgRating="4.5"
    var skills=ArrayList<String>()
    var userImage=""
}