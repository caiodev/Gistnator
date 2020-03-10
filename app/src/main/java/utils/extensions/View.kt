package utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import br.com.caiodev.gistnator.R
import com.google.android.material.snackbar.Snackbar
import utils.snackBar.CustomSnackBar

@Suppress("unused")
fun Context.applyViewVisibility(view: View, visibility: Int) {
    view.visibility = visibility
}

fun Context.changeDrawable(target: ImageView, newDrawable: Int) {
    target.setImageDrawable(
        ContextCompat.getDrawable(
            applicationContext,
            newDrawable
        )
    )
}

@Suppress("unused")
inline fun Context.showSnackBar(
    fragmentActivity: FragmentActivity, message: String, crossinline onDismissed: () -> Unit
) {
    Snackbar.make(
        fragmentActivity.findViewById(android.R.id.content),
        message,
        Snackbar.LENGTH_SHORT
    ).addCallback(object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            onDismissed.invoke()
        }
    }).show()
}

fun showInternetConnectionStatusSnackBar(applicationContext: Context, customSnackBar: CustomSnackBar, isInternetConnectionAvailable: Boolean) {
    with(customSnackBar) {
        if (isInternetConnectionAvailable) {
            setText(applicationContext.getString(R.string.back_online_success_message)).setBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.green_700
                )
            )
            if (isShown) dismiss()
        } else {
            setText(applicationContext.getString(R.string.no_connection_error)).setBackgroundColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.red_700
                )
            )
            show()
        }
    }
}

fun Context.hideKeyboard(editText: EditText) {
    with(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) {
        hideSoftInputFromWindow(editText.applicationWindowToken, 0)
    }
}