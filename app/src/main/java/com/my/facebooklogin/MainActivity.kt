package com.my.facebooklogin

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginButton: LoginButton
    private lateinit var  loginManager: LoginManager
    private lateinit var name:TextView
    private lateinit var lasName:TextView
    private lateinit var emailTv:TextView
    private lateinit var image:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        printHashKey()
       FacebookSdk.sdkInitialize(this);
//        AppEventsLogger.activateApp(baseContext as Application);
        loginButton = findViewById(R.id.login_button)
        name = findViewById(R.id.tv_name)
        lasName = findViewById(R.id.tv_lastName)
        emailTv = findViewById(R.id.tv_email)



        callbackManager = CallbackManager.Factory.create()
        loginButton.setPermissions(arrayListOf("email", "public_profile"))




        loginFunc()

    }
    private fun loginFunc(){
        loginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.d(TAG, "onCancel: called")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "onError: called")
                }

                override fun onSuccess(result: LoginResult) {
                    val userId = result.accessToken.userId
                    Log.d(TAG, "onSuccess: userId $userId")


                    val bundle = Bundle()
                    bundle.putString("fields", "id, email, first_name, last_name, gender")





                    //Graph API to access the data of user's facebook account

                    val request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        object : GraphRequest.GraphJSONObjectCallback {

                            override fun onCompleted(obj: JSONObject?, response: GraphResponse?) {
                                Log.v("Login Success", response.toString())
                                try {

                                    Log.d(TAG, "onSuccess: fbObject $obj")

                                    val userID = obj?.getString("id")
                                    val firstName = obj?.getString("first_name")
                                    val lastName = obj?.getString("last_name")
                                    val email = obj?.getString("email")


                                    Log.d(TAG, "onSuccess: firstName $firstName")
                                    Log.d(TAG, "onSuccess: lastName $lastName")
//                        Log.d(TAG, "onSuccess: gender $gender")
                                    Log.d(TAG, "onSuccess: email $email")
                                    name.text = firstName
                                    lasName.text = lastName
                                    emailTv.text = email

                                    val profilePicUrl: String =
                                        obj!!.getJSONObject("picture").getJSONObject("data").getString("url")
                                    Log.i(TAG, "onSuccess:profile $profilePicUrl");
                                    val fb_url =  URL(profilePicUrl)
                                    Glide.with(this@MainActivity).load(fb_url).into(image);






                                } //If no data has been retrieve throw some error
                                catch (e: JSONException) {

                                }
                            }
//


                        })
                    request.parameters.putString("fields", "id, email, first_name, last_name, gender,age_range,picture,link")
                    request.executeAsync()
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }



    fun printHashKey(){
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }
}
private fun GraphRequest.parameters(bundle: Bundle) {

}
