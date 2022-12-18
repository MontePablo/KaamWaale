package com.example.kaamwaale.models

import android.widget.ArrayAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Order {
    var orderId=""
    var quantity=""
    var title=""
    var dateTime=""
    var image=""
    var packName=""
    var packInfo=""
    var packPrice=""
    var type=""
    var status=""
    var users=ArrayList<String>()
    var sellerId=""
    var clientId=""
    var deliveryInstruction=""
    var deliveryImages=ArrayList<String>()
    var sellerConfirm=false
    var clientConfirm=false
}