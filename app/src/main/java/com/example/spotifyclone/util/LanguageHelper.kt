import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.preference.PreferenceManager
import android.util.Log
import java.util.Locale

object LanguageHelper {

    private const val PREF_SELECTED_LANGUAGE = "selected_language"
    private const val DEFAULT_LANGUAGE = "en"

    fun updateLocale(context: Context?): Context {
        val languageCode = getSavedLanguage(context)
        return setAndSaveLocale(context!!, languageCode)
    }

    private fun saveLanguage(context: Context, languageCode: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(PREF_SELECTED_LANGUAGE, languageCode)
        editor.apply()
    }

    fun getSavedLanguage(context: Context?): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString(PREF_SELECTED_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setLocale(context: Context?, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        Log.d("LanguageHelper", "Setting locale to $languageCode")
        val configuration = Configuration(context?.resources?.configuration)
        configuration.setLocale(locale)
        context?.resources?.updateConfiguration(configuration, context.resources.displayMetrics)

        return context ?: ContextWrapper(context)
    }

    fun setAndSaveLocale(context: Context, languageCode: String):Context {
        setLocale(context, languageCode)
        saveLanguage(context,languageCode)

        return context

    }
}
