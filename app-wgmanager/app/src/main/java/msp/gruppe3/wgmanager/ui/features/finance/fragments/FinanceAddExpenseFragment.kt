package msp.gruppe3.wgmanager.ui.features.finance.fragments

import android.app.AlertDialog
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import msp.gruppe3.wgmanager.R
import msp.gruppe3.wgmanager.common.TokenUtil
import msp.gruppe3.wgmanager.databinding.FragmentFinanceAddExpenseBinding
import msp.gruppe3.wgmanager.enums.RecurringExpenses
import msp.gruppe3.wgmanager.models.dtos.finance.FinanceExpenseDto
import msp.gruppe3.wgmanager.services.FinanceService
import msp.gruppe3.wgmanager.ui.features.finance.FinanceViewModel
import retrofit2.HttpException
import java.util.*


private const val TAG = "FinanceAddExpenseFragment"

/**
 * Add and Edit expense
 *
 * @author Marcello Alte
 */
class FinanceAddExpenseFragment : Fragment() {
    private var _binding: FragmentFinanceAddExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var financeViewModel: FinanceViewModel

    // Image upload
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private var imageUri: Uri? = null

    // For edit
    private var expenseToUpdate: FinanceExpenseDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        financeViewModel = ViewModelProvider(requireActivity())[FinanceViewModel::class.java]

        registerPhotoPicker()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinanceAddExpenseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Init recurring options and initialize the dropdown menu
        val recurringOptions = resources.getStringArray(R.array.recurring_options)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, recurringOptions)
        binding.financeAddExpenseRecurringDropdownAutoCompleteTextView.setAdapter(arrayAdapter)

        // Init button
        binding.financeAddExpenseSubmit.setOnClickListener { submitExpense() }
        binding.financeAddExpenseImageButton.setOnClickListener { pickImage() }

        // For edit: Update Expense if id argument is passed
        val id = arguments?.getString("id")
        if ((id != null) && id.isNotEmpty()) {
            Log.e(TAG, "Got id from nav argument $id")
            getExpenseByIdRequest(UUID.fromString(id))
        } else {
            // Make delete button visible
            binding.financeAddExpenseDeleteButton.visibility = INVISIBLE
        }

        val description = arguments?.getString("description")
        if ((description != null) && description.isNotEmpty()) {
            Log.e(TAG, "Got id from nav argument $description")
            binding.financeAddExpenseDescription.setText(description)
        }
        return root
    }

    private fun registerPhotoPicker() {
        // Registers a photo picker activity launcher in single-select mode.
        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                binding.financeAddExpenseImageButton.setImageURI(uri)
                imageUri = uri
            } else {
                // Reset image
                Log.d("PhotoPicker", "No media selected")
                val drawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_menu_camera)
                binding.financeAddExpenseImageButton.setImageDrawable(drawable)
                imageUri = null
            }
        }
    }

    private fun pickImage() {
        // Launch the photo picker and allow the user to choose only images.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /**
     * Helper method to get recurring object
     */
    private fun getRecurringDetails(recurringOption: String): RecurringExpenses? {
        Log.d(TAG, "getRecurringDetails: $recurringOption")
        return when (recurringOption) {
            "Weekly" -> RecurringExpenses.WEEKLY
            "Monthly" -> RecurringExpenses.MONTHLY
            else -> null
        }
    }

    /**
     * Helper method to get recurring string
     */
    private fun getRecurringString(recurring: RecurringExpenses): String {
        Log.d(TAG, "getRecurringString: $recurring")
        return when (recurring) {
            RecurringExpenses.WEEKLY -> "Weekly"
            RecurringExpenses.MONTHLY -> "Monthly"
        }
    }

    /**
     * Create the expense dto model
     */
    private fun buildNewExpenseModel(): FinanceExpenseDto {
        val description: String = binding.financeAddExpenseDescription.text.toString()
        val price: Double = binding.financeAddExpensePrice.text.toString().toDouble()
        val recurring: RecurringExpenses? =
            getRecurringDetails(binding.financeAddExpenseRecurringDropdownAutoCompleteTextView.text.toString())
        val image: ByteArray? = when (imageUri) {
            null -> null
            else -> getByteArrayByImageUri(imageUri!!)
        }

        val newExpense = FinanceExpenseDto(
            null,
            description,
            price,
            null,
            recurring,
            null,
            image
        )

        // For edit
        if (expenseToUpdate != null) {
            newExpense.id = expenseToUpdate!!.id
            newExpense.payer = expenseToUpdate!!.payer
            newExpense.expenseDate = expenseToUpdate!!.expenseDate
        }

        Log.d(TAG, "Expense model out: $newExpense")
        return newExpense
    }

    /**
     * Send the new expense to the server and update the view model with the new data
     */
    private fun addExpenseRequest(expense: FinanceExpenseDto) {
        val wgId = financeViewModel.getWgId(requireActivity())

        if (wgId != null) {
            val token = TokenUtil.getTokenByActivity(requireActivity())
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                // Differentiate between add and update
                val response: FinanceExpenseDto?
                if (expense.id == null) {
                    Log.d(TAG, "Create expense $expense")
                    response = financeService.addExpense(wgId, expense)
                } else {
                    Log.d(TAG, "Update expense $expense")
                    response = financeService.updateExpense(wgId, expense, null)
                }
                try {
                    if (response != null) {
                        financeViewModel.getExpensesFromServer(requireActivity())
                        activity?.runOnUiThread {
                            findNavController().navigate(R.id.financeFragment)
                        }
                    } else {
                        showToast("Sorry something went wrong.")
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        } else {
            showToast("Sorry something went wrong with your wgId.")
        }
    }

    private fun submitExpense() {
        Log.d(TAG, "submitExpense")

        // Error check
        if (binding.financeAddExpenseDescription.text.trim().isEmpty()) {
            showToast("Please provide a description.")
            return
        }
        val priceBinding = binding.financeAddExpensePrice.text
        if (priceBinding.isEmpty() || priceBinding.toString().toDouble() <= 0.0) {
            showToast("Please provide a price.")
            return
        }

        addExpenseRequest(buildNewExpenseModel())
    }

    // Set image
    private fun getByteArrayByImageUri(imageUri: Uri): ByteArray? {
        val contentResolver: ContentResolver = requireContext().contentResolver ?: return null
        return contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
    }

    private fun deleteExpense() {
        val wgId = financeViewModel.getWgId(requireActivity())

        val expenseId = expenseToUpdate?.id

        if (wgId != null || expenseId == null) {
            val token = TokenUtil.getTokenByActivity(requireActivity())
            val financeService = FinanceService(token)

            CoroutineScope(Dispatchers.Main).launch {
                val response = financeService.deleteExpense(wgId!!, expenseId!!, null) //TODO add future
                try {
                    Log.d(TAG, "Response from deleteExpense: $response")
                    if ((response != null) && response.success) {
                        financeViewModel.getExpensesFromServer(requireActivity())
                        activity?.runOnUiThread {
                            findNavController().navigate(R.id.financeFragment)
                        }
                    } else {
                        showToast("Sorry something went wrong.")
                    }
                } catch (e: HttpException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (e: Throwable) {
                    Log.e(TAG, e.toString())
                }
            }
        } else {
            showToast("Sorry something went wrong.")
        }
    }

    /**
     * Sets values of the expense to be updated
     */
    private fun setEditExpense(editExpense: FinanceExpenseDto) {
        // Set description
        binding.financeAddExpenseDescription.setText(editExpense.description)

        // Set price
        binding.financeAddExpensePrice.setText(String.format("%.2f", editExpense.price))

        // Set image
        if (editExpense.image != null) {
            setImageViewWithByteArrayAsBitMap(editExpense.image!!, binding.financeAddExpenseImageButton)
        }

        // Set recurring
        val recurringString = editExpense.recurring?.let { getRecurringString(it) }
        binding.financeAddExpenseRecurringDropdownAutoCompleteTextView.setText(
            recurringString,
            false
        )

        // Make delete button visible
        binding.financeAddExpenseDeleteButton.visibility = VISIBLE
        binding.financeAddExpenseDeleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you want to delete this expense?")
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, id ->
                    deleteExpense()
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show() }

        // Change submit button text
        binding.financeAddExpenseSubmit.text = getString(R.string.save)
    }

    /**
     * get the expense to be updated
     */
    private fun getExpenseByIdRequest(id: UUID) {
        val token = TokenUtil.getTokenByActivity(requireActivity())
        val financeService = FinanceService(token)

        CoroutineScope(Dispatchers.Main).launch {
            val response = financeService.getExpenseById(id)
            try {
                Log.d(TAG, "Response from getExpenseByIdRequest: $response")
                if (response != null) {
                    expenseToUpdate = response
                    setEditExpense(response)
                } else {
                    expenseToUpdate = null
                    showToast("Sorry something went wrong.")
                }
            } catch (e: HttpException) {
                Log.e(TAG, "Exception ${e.message}")
            } catch (e: Throwable) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun setImageViewWithByteArrayAsBitMap(byteArray: ByteArray, image: ImageView) {
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        image.setImageBitmap(
            Bitmap.createScaledBitmap(
                bmp,
                image.getWidth(),
                image.getHeight(),
                false
            )
        )
    }

    /**
     * Show toast helper method
     * Length toast shown: LENGTH_SHORT
     */
    private fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, length).show()
    }
}