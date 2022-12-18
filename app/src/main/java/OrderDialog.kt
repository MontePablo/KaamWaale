import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import com.example.kaamwaale.Login
import com.example.kaamwaale.MainActivity
import com.example.kaamwaale.Orders
import com.example.kaamwaale.R
import com.example.kaamwaale.daos.FirebaseDao
import com.example.kaamwaale.daos.FirebaseDao.TAG
import com.example.kaamwaale.daos.OrderDao
import com.example.kaamwaale.databinding.NewOrderBinding
import com.example.kaamwaale.models.Gig
import com.example.kaamwaale.models.Order
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.acos

object OrderDialog: PaymentResultListener {
    lateinit var activity: Activity
    lateinit var gig: Gig
    lateinit var context:Context
    lateinit var layoutInflater: LayoutInflater

    var packNo: Int=0
    var quantt:Int=1
    fun processOrder(activity:Activity,context:Context, gig: Gig?, layoutInflater: LayoutInflater) {
        Log.d(TAG,"main func start")
        Log.d(TAG,"gig: ${gig!!.title}")
        Log.d(TAG,"context:$context")
        Log.d(TAG,"activity: $activity")

        this.activity=activity
        this.gig=gig!!
        this.context=context
        this.layoutInflater=layoutInflater
        var dialogBuilder= AlertDialog.Builder(context)
        var viewBinding= NewOrderBinding.inflate(layoutInflater)
        dialogBuilder.setView(viewBinding.root)
        val dialog=dialogBuilder.create()
        dialog.show()

        var price= gig!!.packages[0].price
        var unit=gig!!.packages[0].unit

        viewBinding.orderSpin.adapter=packageToAdapter()
        viewBinding.orderSpin.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                packNo=p2
                price=gig!!.packages[p2].price
                viewBinding.orderPrice.text=gig!!.packages[p2].price.toString()
                viewBinding.orderUnit.text=gig!!.packages[p2].unit
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d(FirebaseDao.TAG,"")
            }
        }
        val arrayAdapter=ArrayAdapter(context,R.layout.dropdown_item, arrayOf(1,2,3,4,5,6,7,8,9,10))
        viewBinding.orderQuantity.adapter=arrayAdapter
        viewBinding.orderQuantity.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        quantt=viewBinding.orderQuantity.selectedItem as Int
                    }
            override fun onNothingSelected(p0: AdapterView<*>?) {
              Log.d(TAG,"")
            }
        }
        viewBinding.orderUnit.text=unit
        viewBinding.orderGotoPayment.setOnClickListener(View.OnClickListener { payment(price.toFloat()) })
        viewBinding.orderCancel.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })
        Log.d(TAG,"main func finished")
    }
    fun payment(amount:Float){
        val activity: Activity = activity

        val co = Checkout()
        co.setKeyID("rzp_test_xTHEX7CRZJHVvn")
        try {
            val options = JSONObject()
            options.put("name", "KaamWaale")
            options.put("description", "Order Amount")
            options.put("send_sms_hash", true)
            options.put("allow_rotation", true)
            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency", "INR")
            options.put("amount", amount*100* quantt)
            val preFill = JSONObject()
            preFill.put("email", "test@razorpay.com")
            preFill.put("contact", "9876543210")
            options.put("prefill", preFill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
                .show()
            Log.d(FirebaseDao.TAG,"error in payment ${e.localizedMessage}")
            e.printStackTrace()
        }
        Log.d(TAG,"pay func finished")

    }
    fun packageToAdapter(): ArrayAdapter<String> {
        var arr=ArrayList<String>()
        for(r in gig!!.packages){
            arr.add(r.title)
        }
        val adapter= ArrayAdapter(context, R.layout.dropdown_item,arr)
        return adapter
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.d(TAG,"payment success")
        Toast.makeText(context,"payment done! $p0",Toast.LENGTH_SHORT).show()
        passOrder()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.d(TAG,"payment failed")
        Toast.makeText(context,"payment failed!$p0",Toast.LENGTH_SHORT).show()
    }
    fun passOrder(){
        var order=Order()
        order.run {
            quantity= quantt.toString()
            title=gig.title
            image=gig.images[0]
            packInfo=gig!!.packages[packNo].description
            packPrice=gig!!.packages[packNo].price
            packName=gig!!.packages[packNo].title
            users.add(FirebaseDao.auth.uid.toString())
            users.add(gig!!.uid)
            sellerId=gig!!.uid
            clientId=FirebaseDao.auth.uid.toString()
            status="not yet delivered"
            type=gig!!.serviceType
            val calendar: Calendar = Calendar.getInstance() // Returns instance with current date and time set
            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
            dateTime=formatter.format(calendar.time)
        }
        OrderDao.addOrder(order)
            .addOnSuccessListener {
                Toast.makeText(context, "order complete!", Toast.LENGTH_SHORT).show()
                Log.d(TAG,"order upload success")
            }.addOnFailureListener {
                Log.d(TAG,"order upload failed ${it.localizedMessage}")
            }
        startActivity(context,Intent(context, Orders::class.java),null)
    }
}
