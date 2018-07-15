package com.guendeli.firebasechat.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.ListenerRegistration
import com.guendeli.firebasechat.AppConstants
import com.guendeli.firebasechat.ChatActivity

import com.guendeli.firebasechat.R
import com.guendeli.firebasechat.recyclerView.item.PersonItem
import com.guendeli.firebasechat.utils.firestoreUtils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import org.jetbrains.anko.support.v4.startActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class PeopleFragment : Fragment() {

    private lateinit var  userListenerRegistration: ListenerRegistration;
    private var shouldInitRecyclerView = true;
    private lateinit var peopleSection: Section;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        userListenerRegistration = firestoreUtils.addUsersListener(this.activity!!, this::updateRecyclerView)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreUtils.removeListener(userListenerRegistration);
        shouldInitRecyclerView = true;
    }
    private fun updateRecyclerView(items : List<Item>){
        fun init(){
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick);
                }
            }
            shouldInitRecyclerView = false;
        }
        fun updateItems() = peopleSection.update(items);

        if(shouldInitRecyclerView){
            init();
        } else {
            updateItems();
        }
    }

    private val onItemClick = OnItemClickListener{item, view ->
        if(item is PersonItem){
            startActivity<ChatActivity>(
                AppConstants.USER_NAME to item.person.name,
                AppConstants.USER_ID to item.userId
            )
        }
    }
}
