package pk.zaman.e_commerce

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Executors.newSingleThreadScheduledExecutor().schedule({
            OpenLogin()
        },1500,TimeUnit.MILLISECONDS)
    }
    private fun OpenLogin() {
        val preferences = Util.getPref(applicationContext)
        if (preferences.getString("username", "") == "" || preferences.getString(
                "pass",
                ""
            ) == ""
        ) {
            startActivity(Intent(this@SplashActivity, ConfirmerActivity::class.java))
        } else {
            if (preferences.getString("username", "") == "admin") {
                startActivity(Intent(this@SplashActivity, AdminActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, ProductActivity::class.java))
            }
        }
        finish()
    }
}