package com.example.rxdatavalidation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class MainViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val emailRegex = Regex("""^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\w+\.)+\w+$""")
    private val phoneRegex = Regex("""^01(?:0|1|[6-9])-(\d{3}|\d{4})-\d{4}$""")
    private val birthDayRegex = Regex("""^(19[0-9][0-9]|20\d{2})-(0[0-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$""")

    private val emailSubject = BehaviorSubject.createDefault("")
    private val phoneSubject = BehaviorSubject.createDefault("")
    private val birthdaySubject = BehaviorSubject.createDefault("")

    var emailChecked = MutableLiveData<Boolean>()
    val phoneChecked = MutableLiveData<Boolean>()
    val birthDayChecked = MutableLiveData<Boolean>()
    val loginBtnEnabled = MutableLiveData<Boolean>()

    init {
        compositeDisposable.addAll(
            Observable.combineLatest(
                emailValidation(),
                phoneValidation(),
                birthDayValidation(),
                { isEmailChecked, isPhoneChecked, isBirthDayChecked
                    -> isEmailChecked && isPhoneChecked && isBirthDayChecked })
                .subscribe { loginBtnEnabled.value = it },

            emailValidation().subscribe { emailChecked.value = it },
            phoneValidation().subscribe { phoneChecked.value = it },
            birthDayValidation().subscribe { birthDayChecked.value = it }
        )
    }

    fun setEmailText(text : CharSequence) {
        emailSubject.onNext(text.toString())
    }
    fun setPhoneText(text : CharSequence) {
        phoneSubject.onNext(text.toString())
    }
    fun setBirthDayText(text : CharSequence) {
        birthdaySubject.onNext(text.toString())
    }

    fun getEmailText(): CharSequence = emailSubject.value
    fun getPhoneText(): CharSequence = phoneSubject.value
    fun getBirthDayText(): CharSequence = birthdaySubject.value

    private fun emailValidation() = emailSubject.map { emailRegex.matches(it) }
    private fun phoneValidation() = phoneSubject.map { phoneRegex.matches(it) }
    private fun birthDayValidation() = birthdaySubject.map { birthDayRegex.matches(it) }
}