package com.cornellappdev.android.eatery.page.brb

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.Checkable
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cornellappdev.android.eatery.R

class BRBLoginView() : AppCompatActivity(), TextView.OnEditorActionListener {
    lateinit var netidPrompt: TextView
    lateinit var passwordPrompt: TextView
    var netidTextField: EditText? = null
    val headerLabel: TextView? = null
    var passwordTextField: EditText? = null
    var loginButton: Button? = null

    val perpetualLoginButton: Checkable? = null
    val privacyStatementButton: Button? = null
    val privacyStatementTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brb_login)

        // TODO Privacy statment
        // SWIFT: privacyStatementTextView?.editableText?.insert(0, "\n\nPrivacy Statement\n\n\nWhen you log in using our system, we will use your credentials solely to fetch your account information on your behalf. Your credentials will be stored safely on this device in a manner similar to how a web browser might cache your login information.\n\nYour netid and password will never leave your device, and will never be stored on our servers or viewed by anyone on our team.\n\nYou may log out of your account at any time to erase all of your account information from this device.\n\n\nTap Anywhere To Dismiss"
        // SWIFT: privacyStatementButton.setText("Privacy Statement")//, for:.normal)

        val netidPrompt = findViewById(R.id.netidPrompt) as EditText
        netidPrompt.setOnEditorActionListener(this)
        this.netidTextField = netidPrompt;
        val passwordPrompt = findViewById(R.id.passwordPrompt) as EditText
        passwordPrompt.setOnEditorActionListener(this)
        this.passwordTextField = passwordPrompt;

        val loginButton = findViewById(R.id.loginButton) as Button
        loginButton.setOnClickListener({
            login()
        })
        this.loginButton = loginButton;
    }

    fun privacyStatementButtonPressed() {
        // TODO Show privacy statement
    }

    fun dismissPrivacyStatement(sender: Button) {
        // TODO
    }

    fun keepMeSignedIn() { // toggle
        if (perpetualLoginButton != null) {
            perpetualLoginButton.isChecked = !perpetualLoginButton.isChecked

            if (!perpetualLoginButton.isChecked) {
                // TODO Save change to Android Preferences
                // SWIFT: UserDefaults.standard.removeObject(forKey: BRBAccountSettings. SAVE_LOGIN_INFO)
            }
        }
    }


    fun login() {
        val netid = (netidTextField?.text ?: "").toString()
        val password = (passwordTextField?.text ?: "").toString()

        if (netid.length > 0 && password.length > 0) {
            headerLabel?.text = "Logging in... this may take a minute."
            headerLabel?.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            val returnIntent = Intent()
            returnIntent.putExtra("netid", netid)
            returnIntent.putExtra("password", password)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()

            // TODO: Save user's preference for storing their netid/password
            // SWIFT: UserDefaults.standard.set(perpetualLoginButton.isSelected, forKey: BRBAccountSettings. SAVE_LOGIN_INFO)
        } else {
            // TODO is focus comparable to "first responding" status
            if (netid.length == 0) {
                netidTextField?.requestFocus();
            } else {
                passwordTextField?.requestFocus();
            }
        }
    }

    fun loginFailedWithError(error: String) {
        headerLabel?.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        headerLabel?.text = error
    }

    fun textFieldShouldReturn(textField: EditText): Boolean {
        if (textField == passwordTextField) {
            login()
        }
        return true
    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        return false;
    }
}