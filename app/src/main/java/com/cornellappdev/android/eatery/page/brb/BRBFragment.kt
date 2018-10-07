package com.cornellappdev.android.eatery.page.brb

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cornellappdev.android.eatery.R
import com.cornellappdev.android.eatery.databinding.DiningHistoryPaymentEntryBinding
import com.cornellappdev.android.eatery.model.EateryModel
import com.cornellappdev.android.eatery.page.EateryTabFragment
import java.util.*

class BRBFragment : EateryTabFragment(), BRBLoginViewDelegate, BRBAccountSettingsDelegate, BRBConnectionDelegate {
    private var accountBalance: AccountBalance? = null

    lateinit var connectionHandler: BRBConnectionHandler
    lateinit var mLoginButton: Button;
    lateinit var mBRBAmountText: TextView;
    lateinit var mLaundryAmountText: TextView;
    lateinit var mSwipesAmountText: TextView;
    lateinit var mCityBucksAmountText: TextView;
    lateinit var mHistoryList: RecyclerView

    var loggedIn = false
    var tableView: TableLayout? = null
    val timeout = 30.0 // seconds
    var time = 0.0 // time of request
    var diningHistory = arrayOf<HistoryEntry>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        val fragment = inflater.inflate(R.layout.fragment_brb, container, false);
        mLoginButton = fragment.findViewById<Button>(R.id.brbLogin);
        mLoginButton.setOnClickListener({ v: View ->
            onClick(v)
        })
        mSwipesAmountText = fragment.findViewById(R.id.swipesAmountText);
        mBRBAmountText = fragment.findViewById(R.id.brbAmountText);
        mCityBucksAmountText = fragment.findViewById(R.id.citybucksAmountText);
        mLaundryAmountText = fragment.findViewById(R.id.laundryAmountText);
        mHistoryList = fragment.findViewById(R.id.historyRecyclerView);
        return fragment
    }

    fun onClick(v: View) {
        val context = getContext();
        if (context != null) {
            val handler = BRBConnectionHandler(context)
            handler.init()
            handler.delegate = this
            connectionHandler = handler
            addLoginView()
        }
    }

    fun addLoginView() {
        val intent = Intent(context?.applicationContext, BRBLoginView::class.java)
        startActivityForResult(intent, 1)

        // TODO save netid/password

        //if (netid?.count ?: 0 > 0 && password?.count ?: 0 > 0) {
        // TODO retrieve saved netid/password
        //}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO These shouldn't be passed like this for security reasons, this is a dirty
        // TODO quick implementation.
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val net = data!!.getStringExtra("netid")
                val pass = data!!.getStringExtra("password")
                brbLoginViewClickedLogin(net, pass)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    fun userClickedProfileButton() {
        // TODO Implement profile button
    }

    fun timer(timer: Timer) {
        // TODO Investigate iOS' reasons behind this timer
    }

    fun setupAccountPage() {
        mCityBucksAmountText.text = "$${accountBalance!!.cityBucks}"
        mLaundryAmountText.text = "$${accountBalance!!.laundry}"
        mBRBAmountText.text = "$${accountBalance!!.brbs}"

        if (connectionHandler.accountBalance.swipes != "") {
            mSwipesAmountText.text = accountBalance!!.swipes
        }

        class HistoryEntryHolder(val item: DiningHistoryPaymentEntryBinding) : RecyclerView.ViewHolder(item.root) {
            var entry: HistoryEntry? = null
            fun bind(entry: HistoryEntry) {
                this.item.historyEntry = entry;
                this.entry = entry;
            }
        }

        class HistoryListAdapter : RecyclerView.Adapter<HistoryEntryHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryEntryHolder {
                val inflator = LayoutInflater.from(context);
                val historyEntryBinding = DiningHistoryPaymentEntryBinding.inflate(inflator, parent, false)
                return HistoryEntryHolder(historyEntryBinding)
            }

            override fun getItemCount(): Int {
                return diningHistory.count()
            }

            override fun onBindViewHolder(holder: HistoryEntryHolder, position: Int) {
                holder.bind(diningHistory.get(position));
            }
        }

        mHistoryList.adapter = HistoryListAdapter()
    }

    override fun loginFailed(error: String) {
        System.out.println("failed: " + error);
        // TODO Report errors to loginView
        //  loginView?.loginFailedWithError(error)
    }

    override fun updateHistory(entries: Array<HistoryEntry>) {
        this.diningHistory = entries
        setupAccountPage()
    }

    override fun finishedLogin(netid: String, password: String) {
        loggedIn = true
    }

    override fun setAccountBalance(accountBalance: AccountBalance) {
        this.accountBalance = accountBalance;
        setupAccountPage();
    }

    fun brbAccountSettingsDidLogoutUser(brbAccountSettings: BRBAccountSettingsViewController) {
        val c = context;
        if (c != null) {
            val handler = BRBConnectionHandler(c)
            handler.init()
            connectionHandler = handler
            connectionHandler.delegate = this
            addLoginView()
        }

    }

    override fun brbLoginViewClickedLogin(netid: String, password: String) {
        // TODO Monitor request time and timeout
        connectionHandler.netid = netid
        connectionHandler.password = password
        connectionHandler.handleLogin()
    }

    override fun onDataLoaded(eateries: MutableList<EateryModel>?) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false //To change body of created functions use File | Settings | File Templates.
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false //To change body of created functions use File | Settings | File Templates.
    }
}
