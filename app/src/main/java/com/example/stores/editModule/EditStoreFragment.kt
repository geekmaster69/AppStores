package com.example.stores.editModule

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.stores.R
import com.example.stores.common.entities.StoreEntity
import com.example.stores.databinding.FragmentEditStoreBinding
import com.example.stores.editModule.viewModel.EditStoreViewModel
import com.example.stores.mainModule.MainActivity
import com.google.android.material.textfield.TextInputLayout

class EditStoreFragment : Fragment() {
    private lateinit var nBinding: FragmentEditStoreBinding
    //MVVM
    private lateinit var mEditStoreViewModel: EditStoreViewModel
    private var mActivity: MainActivity? = null
    private var mIsEditMode: Boolean = false
    private lateinit var mStoreEntity: StoreEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mEditStoreViewModel = ViewModelProvider(requireActivity()).get(EditStoreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        nBinding = FragmentEditStoreBinding.inflate(layoutInflater, container, false)
        return nBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupViewModel()

        setHasOptionsMenu(true)
        setupTextFields()
    }

    private fun setupViewModel() {
        mEditStoreViewModel.getStoreSelected().observe(viewLifecycleOwner){
            mStoreEntity = it ?: StoreEntity()
            if (it != null){
                mIsEditMode = true
                setUiStore(it)
            }else{
                mIsEditMode = false
            }
        }
        setupActionBar()

        mEditStoreViewModel.getResult().observe(viewLifecycleOwner){ result ->
            hideKeyboard()

            when(result){
                is StoreEntity ->{
                    val msgRes = if (result.id == 0L) "La tienda se agrego correctamente"
                                 else "Se actualizo la tienda"
                    mEditStoreViewModel.setStoreSelect(mStoreEntity)
                    Toast.makeText(activity, msgRes, Toast.LENGTH_SHORT).show()
                    mActivity?.onBackPressed()
                }
            }
        }
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
                if (validateFields(nBinding.tilPhotoUrl, nBinding.tilPhone, nBinding.tilName)){
                    with(mStoreEntity){
                        name = nBinding.etName.text.toString().trim()
                        phone = nBinding.etPhone.text.toString().trim()
                        website = nBinding.etWebsite.text.toString().trim()
                        photoUrl = nBinding.etPhotoUrl.text.toString().trim()
                    }

                    if (mIsEditMode)
                        mEditStoreViewModel.updateStore(mStoreEntity)
                    else mEditStoreViewModel.saveStore(mStoreEntity)

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
        mEditStoreViewModel.setResult(Any())
        mEditStoreViewModel.setShowFab(true)
        super.onDestroy()
    }
}