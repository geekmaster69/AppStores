package com.example.stores

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.databinding.FragmentEditStoreBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreFragment : Fragment() {
    private lateinit var nBinding: FragmentEditStoreBinding
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private var mStoreEntity: StoreEntity? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        nBinding = FragmentEditStoreBinding.inflate(layoutInflater, container, false)
        return nBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getLong(getString(R.string.arg_id),0)
        if (id != null && id != 0L){
            mIsEditMode = true
            getStore(id)
        }else{
            mIsEditMode = false
            mStoreEntity = StoreEntity(name = "", phone = "", photoUrl = "")
        }

        setupActionBar()

        setHasOptionsMenu(true)

        setupTextFields()
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (mIsEditMode) mActivity?.supportActionBar?.title = "Editar Tienda"
                         else mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_add)

    }

    private fun setupTextFields() {

        with(nBinding) {

            etPhotoUrl.addTextChangedListener {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
            etPhone.addTextChangedListener { validateFields(tilPhone) }
            etName.addTextChangedListener { validateFields(tilName) }
        }
    }

    private fun loadImage(url: String){
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(nBinding.imgPhoto)

    }

    private fun getStore(id: Long) {
        doAsync {
            mStoreEntity = StoreApplication.database.storDao().getStoreById(id)
            uiThread { if (mStoreEntity != null) setUiStore(mStoreEntity!!)}
        }
    }

    private fun setUiStore(storeEntity: StoreEntity) {
        with(nBinding){
            etName.text = storeEntity.name.editable()
            etPhone.text = storeEntity.phone.editable()
            etWebsite.text = storeEntity.website.editable()
            etPhotoUrl.text = storeEntity.photoUrl.editable()
        }
    }

    private fun String.editable(): Editable = Editable.Factory.getInstance().newEditable(this)

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
                if (mStoreEntity != null && validateFields(nBinding.tilPhotoUrl, nBinding.tilPhone, nBinding.tilName)){
                    with(mStoreEntity!!){
                        name = nBinding.etName.text.toString().trim()
                        phone = nBinding.etPhone.text.toString().trim()
                        website = nBinding.etWebsite.text.toString().trim()
                        photoUrl = nBinding.etPhotoUrl.text.toString().trim()
                    }

                    doAsync {
                        if (mIsEditMode) StoreApplication.database.storDao().updateStore( mStoreEntity!!)
                        else mStoreEntity!!.id = StoreApplication.database.storDao().addStore( mStoreEntity!!)

                        uiThread {
                            hideKeyboard()

                            if (mIsEditMode){
                                mActivity?.updateStore(mStoreEntity!!)

                                Toast.makeText(activity, "Se actualizo la tienda", Toast.LENGTH_SHORT).show()

                                mActivity?.onBackPressed()
                            } else {
                                mActivity?.addStore(mStoreEntity!!)

                                Toast.makeText(
                                    mActivity,
                                    "La tienda se agrego correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                mActivity?.onBackPressed()
                            }
                        }
                    }
                }
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun validateFields(vararg textFields: TextInputLayout): Boolean{
        var isValid = true
        for (texField in textFields){
            if (texField.editText?.text.toString().trim().isBlank()){
                texField.error = getString(R.string.helper_required)
                texField.editText?.requestFocus()
                isValid = false
            }else{
                texField.error = null
            }
        }
        return isValid
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