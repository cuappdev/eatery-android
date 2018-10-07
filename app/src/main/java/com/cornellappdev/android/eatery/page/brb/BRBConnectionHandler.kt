package com.cornellappdev.android.eatery.page.brb

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.webkit.WebView
import android.webkit.WebViewClient
import org.threeten.bp.format.DateTimeFormatter
import java.util.regex.Matcher
import java.util.regex.Pattern


class HistoryEntry {
    var description: String = ""
    var timestamp: String = ""
}

class AccountBalance {
    var brbs: String = ""
    var cityBucks: String = ""
    var laundry: String = ""
    var swipes: String = "0"
}

enum class Stages {
    loginScreen,
    loginFailed,
    transition,
    fundsHome,
    diningHistory,
    finished
}

interface BRBConnectionDelegate {
    fun updateHistory(entries: Array<HistoryEntry>)
    fun loginFailed(error: String)
    fun finishedLogin(netid: String, password: String)
    fun setAccountBalance(accountBalance: AccountBalance)
}

val loginURLString = "https://get.cbord.com/cornell/full/login.php"
val fundsHomeURLString = "https://get.cbord.com/cornell/full/funds_home.php"
val diningHistoryURLString = "https://get.cbord.com/cornell/full/history.php"
val updateProfileURLString = "https://get.cbord.com/cornell/full/update_profile.php"
val maxTrials = 3
val trialDelay = 500

class BRBConnectionHandler @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    lateinit var accountBalance: AccountBalance
    var stage: Stages = Stages.loginScreen
    var diningHistory = arrayOf<HistoryEntry>()
    var loginCount = 0
    var netid: String = ""
    var password: String = ""
    var delegate: BRBConnectionDelegate? = null
    var syncHandler = Handler()

    @SuppressLint("SetJavaScriptEnabled")
    fun init() {
        this.setWebViewClient(InternalWebViewClient({ webView: WebView?, str: String? ->
            onPageFinished(webView, str)
        }));
        this.settings.javaScriptEnabled = true;
    }

    /**

    - Gets the HTML for the current web page and runs block after loading HTML into a string

     */
    fun getHTML(function: (String) -> Unit?) {
        // TODO Explore ways to avoid this method of parsing. (the built-in HTML library doesn't work)
        this.evaluateJavascript("(function() { return document.documentElement.outerHTML.toString()})()", { str: String ->
            val dec = unescape(str);
            function.invoke(dec);
        });
    }

    private fun unescape(data: String): String {
        // TODO Use Kotlin libraries for this task
        val matcher = Pattern.compile("\\\\u(\\p{XDigit}{4})").matcher(data)
        val buffer = StringBuffer(data.length)
        while (matcher.find()) {
            val char = Integer.parseInt(matcher.group(1), 16).toChar()
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(char.toString()))
        }
        matcher.appendTail(buffer)
        return buffer.toString().replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\/", "/")
    }

    /**

    - Loads login web page

     */
    fun handleLogin() {
        loginCount = 0
        stage = Stages.loginScreen
        this.loadUrl(loginURLString)
    }

    fun failedToLogin(): Boolean {
        return loginCount > 2
    }

    /**

    - Fetches the HTML for the currently displayed web page and instantiates an DiningHistory array
    using the history information on the page.

    - Does not guarantee that the javascript has finished executing before trying to get dining history.

     */
    fun getDiningHistory() {
        getHTML { html: String ->
            val tableHTMLRegex = "(<tr class=\\\"(?:even|odd|odd first-child)\\\"><td class=\\\"first-child account_name\\\">(.*?)</td><td class=\\\"date_(and_|)time\\\"><span class=\\\"date\\\">(.*?)<\\/span><\\/td><td class=\\\"activity_details\\\">(.*?)<\\/td><td class=\\\"last-child amount_points (credit|debit)\\\" title=\\\"(credit|debit)\\\">(.*?)</td></tr>)"
            val regex = Regex(tableHTMLRegex)
            val matches = regex.findAll(html);
            if (matches.count() > 0) {
                for (match in matches) {
                    val entry = HistoryEntry()

                    val htmlEntry = html.substring(match.range)

                    //val accountName = this.findEntryValue(htmlEntry, fieldName: "account_name")
                    val transDate = this.findEntryValue(htmlEntry, "\"date")
                    val transTime = this.findEntryValue(htmlEntry, "\"time")
                    val amount = this.findEntryValue(htmlEntry, "it")
                    val location = this.findEntryValue(htmlEntry, "details")

                    var dateFormat = "MMMM d, yyyy h:mma"
                    val formatter1 = DateTimeFormatter.ofPattern(dateFormat)

                    dateFormat = "M/d 'at' h:mm a"
                    val formatter = DateTimeFormatter.ofPattern(dateFormat)

                    entry.description = location

                    if (transDate.toByteArray().count() > 0 && transTime.toByteArray().count() > 0) {
                        val date = formatter1.parse(transDate + " " + transTime)

                        if (date != null) {
                            entry.description += "\n " + formatter.format(date)
                        } else {
                            entry.description += "\n ${transDate} at ${transTime}"
                        }
                    }

                    entry.timestamp = if (amount.contains("$")) amount else amount + " swipe"
                    this.diningHistory.plus(entry) //.append(entry)
                }
            }
            this.delegate?.updateHistory(this.diningHistory)
        }
    }

    /**

    - Finds the value that is surrounded by the HTML tag ending with [fieldName">]

     */
    fun findEntryValue(htmlEntry: String, fieldName: String): String {
        val str = fieldName + "\">";
        var curIndex: Int = htmlEntry.indexOf(str) + str.length // TODO

        var value = ""

        while (curIndex < htmlEntry.length - 1 && htmlEntry.get(curIndex) != '<') {
            value += htmlEntry.get(curIndex)
            curIndex += 1
        }

        return value
    }

    class InternalWebViewClient(val handler: (view: WebView?, url: String?) -> Unit) : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            handler.invoke(view, url)
        }
    }

    /**

    - Fetches the HTML for the currently displayed web page and instantiates a new AccountBalance object
    using the account information on the page.

    - Does not guarantee that the javascript has finished executing before trying to get account info.

     */
    fun getAccountBalance(trials: Int = maxTrials) {
        getHTML { html: String ->
            this.accountBalance = AccountBalance()
            val brbHTMLRegex = "<td class=\\\"first-child account_name\\\">BRB.*<\\/td><td class=\\\"last-child balance\">\\$[0-9]+.[0-9][0-9]</td>"
            val cityHTMLRegex = "<td class=\\\"first-child account_name\\\">CB.*<\\/td><td class=\\\"last-child balance\">\\$[0-9]+.[0-9][0-9]</td>"
            val laundryHTMLRegex = "<td class=\\\"first-child account_name\\\">LAU.*<\\/td><td class=\\\"last-child balance\">\\$[0-9]+.[0-9][0-9]</td>"
            val swipesHTMLRegex = "<td class=\\\"first-child account_name\\\">.*0.*<\\/td><td class=\\\"last-child balance\">[1-9]*[0-9]</td>"

            val moneyRegex = "[0-9]+(\\.)*[0-9][0-9]"
            val swipesRegex = ">[1-9]*[0-9]<"

            if (this.stage == Stages.fundsHome) {
                val brbs = this.parseHTML(html, brbHTMLRegex, moneyRegex)
                val city = this.parseHTML(html, cityHTMLRegex, moneyRegex)
                val laundry = this.parseHTML(html, laundryHTMLRegex, moneyRegex)
                val swipes = this.parseHTML(html, swipesHTMLRegex, swipesRegex)

                // Funds does not load immediately
                // Try again with delay until trials run out
                if (brbs == "" && city == "" && laundry == "" && swipes == "" && trials > 0) {
                    syncHandler.postDelayed({
                        this.getAccountBalance(trials - 1);
                    }, trialDelay.toLong())
                } else {

                    println("Done after ${maxTrials + 1 - trials} trials")

                    this.accountBalance.brbs = (if (brbs != "") brbs else "0.00")
                    this.accountBalance.cityBucks = if (city != "") city else "0.00"
                    this.accountBalance.laundry = if (laundry != "") laundry else "0.00"
                    this.accountBalance.swipes = (if (swipes != "") {
                        swipes //String(swipes[swipes.index(after: swipes.startIndex)..<swipes.index(before: swipes.endIndex)]) : ""
                    } else {
                        ""
                    })

                    this.delegate?.setAccountBalance(this.accountBalance);

                    val historyURL = (diningHistoryURLString)
                    this.loadUrl(historyURL)
                }
            }
        }
    }


    /**
     * Makes two passes on an html string with two different
     * regular expressions, returning the inner result
     */
    fun parseHTML(html: String, regex1: String, regex2: String): String {
        val firstPass = this.getFirstRegexMatchFromString(regex1, html)
        val result = this.getFirstRegexMatchFromString(regex2, firstPass)
        return result;
    }

    /**

    - Given a regex string and and a string to match on, returns the first instance of the regex
    string or an empty string if regex cannot be matched.

     */
    fun getFirstRegexMatchFromString(regexString: String, str: String): String {
        val regex = Regex(regexString) // as String, options: .useUnicodeWordBoundaries
        val match = regex.find(str)  // as String, options: NSRegularExpression.MatchingOptions.withTransparentBounds, range: NSMakeRange(0, str.length)
        if (match != null) {
            return str.substring(match.range)
        }
        return ""
    }

    fun login() {
        val javascript = "document.getElementsByName('netid')[0].value = '${netid}';document.getElementsByName('password')[0].value = '${password}';document.forms[0].submit();"
        this.evaluateJavascript(javascript) { result: String ->
            // TODO Check for JS errors and then fail the login
            // this.delegate?.loginFailed(with: error. localizedDescription)
            if (this.failedToLogin()) {
                if (this.url == updateProfileURLString) {
                    this.delegate?.loginFailed("Account needs to be updated")
                }
                this.delegate?.loginFailed("Incorrect netid and/or password")
            } else {
            }
            // }
            this.loginCount += 1
        }
    }

    fun onPageFinished(webView: WebView?, str: String?) {
        this.getStageAndRunBlock(str ?: "") {
            when (this.stage) {
                Stages.loginFailed ->
                    this.delegate?.loginFailed(
                            "Incorrect netid and/or password")
                Stages.loginScreen -> {
                    if (this.loginCount < 3) {
                        this.login()
                    } else {
                    }
                }
                Stages.fundsHome ->
                    this.getAccountBalance()
                Stages.diningHistory ->
                    this.getDiningHistory()
                Stages.transition -> {
                }
                Stages.finished -> {
                }
            }
        }
    }

    /**

    - Gets the stage enum for the currently displayed web page and runs a block after fetching
    the HTML for the page.

    - Does not guarantee Javascript will finish running before the block
    is executed.

     */
    fun getStageAndRunBlock(url: String, block: (stage: Stages) -> Unit?) {
        getHTML { html: String ->
            if (this.failedToLogin()) {
                this.stage = Stages.loginFailed
            } else if (url.contains(updateProfileURLString)) {
                this.stage = Stages.loginFailed
            } else if (html.contains("<h1>CUWebLogin</h1>")) {
                this.stage = Stages.loginScreen
            } else if (url.contains(fundsHomeURLString)) {
                this.stage = Stages.fundsHome
                delegate?.finishedLogin(netid, password);
            } else if (url.contains(diningHistoryURLString)) {
                this.stage = Stages.diningHistory
            } else {
                this.stage = Stages.transition
            }
            //run block for stage
            block.invoke(stage)
        }
    }
}