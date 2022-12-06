package com.example.stores

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {
    private lateinit var nBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        nBinding = FragmentEditStoreBinding.inflate(layoutInflater, container, false)
        return nBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id),0)
        if (id != null && id != 0L){
            Toast.makeText(activity, id.toString(), Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(activity, id.toString(), Toast.LENGTH_SHORT).show()
        }


        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

        setHasOptionsMenu(true)

        nBinding.etPhotoUrl.addTextChangedListener {
            Glide.with(this)
                .load(nBinding.etPhotoUrl.text.toString())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(nBinding.imgPhoto)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save ->{
                val store = StoreEntity(
                    name = nBinding.etName.text.toString().trim(),
                    phone = nBinding.etPhone.text.toString().trim(),
                    website = nBinding.etWebsite.text.toString().trim(),
                    photoUrl = nBinding.etPhotoUrl.text.toString().trim()
                )

                doAsync {
                    store.id = StoreApplication.database.storDao().addtStore(store)
                    uiThread {
                        mActivity?.addStore(store)
                        hideKeyboard()

                        Toast.makeText(mActivity,"La tienda se agrego correctamente", Toast.LENGTH_SHORT).show()
                        mActivity?.onBackPressed()
                    }
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun hideKeyboard(){
        val imm = mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onPause() {
        hideKeyboard()
        super.onPause()
    }



    override fun onDestroy() {

        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        setHasOptionsMenu(false)
        mActivity?.hideFab(true)
        super.onDestroy()
    }


}