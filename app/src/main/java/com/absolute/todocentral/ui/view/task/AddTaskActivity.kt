package com.absolute.todocentral.ui.view.task

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.absolute.todocentral.R
import com.absolute.todocentral.data.models.Task
import com.absolute.todocentral.service.alarm.AlarmHelper
import com.absolute.todocentral.ui.view.base.BaseTaskActivity
import com.absolute.todocentral.utils.PreferenceHelper
import com.absolute.todocentral.utils.toast
import com.absolute.todocentral.vm.AddTaskViewModel
import kotlinx.android.synthetic.main.activity_task_details.*
import java.util.*

class AddTaskActivity : BaseTaskActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar(getString(R.string.create_task))
        showKeyboard(mTitleEditText)

        val position = if (intent.getBooleanExtra("isShortcut", false)) {
            AlarmHelper.getInstance().init(applicationContext)
            PreferenceHelper.getInstance().init(applicationContext)
            PreferenceHelper.getInstance().getInt(PreferenceHelper.NEW_TASK_POSITION)
        } else intent.getIntExtra("position", 0)

        btnTaskConfirm.setOnClickListener {
            when {
                mTitleEditText.length() == 0 -> tilTaskTitle.error = getString(R.string.error_text_input)
                mTitleEditText.text.toString().trim { it <= ' ' }.isEmpty() -> tilTaskTitle.error = getString(R.string.error_spaces)
                else -> {
                    val task = Task().apply {
                        title = mTitleEditText.text.toString()
                        this.position = position
                    }

                    if (tvTaskNote.length() != 0) {
                        task.note = tvTaskNote.text.toString()
                    }

                    if (tvTaskReminder.length() != 0) {
                        task.date = mCalendar.timeInMillis
                    }

                    if (task.date != 0L && task.date <= Calendar.getInstance().timeInMillis) {
                        task.date = 0
                        toast(getString(R.string.toast_incorrect_time))
                    } else if (task.date != 0L) {
                        AlarmHelper.getInstance().setAlarm(task)
                    }
                    val viewModel = createViewModel()
                    viewModel.saveTask(task)
                    finish()
                }
            }
            hideKeyboard(mTitleEditText)
        }
    }

    override fun createViewModel() = ViewModelProviders.of(this).get(AddTaskViewModel(application)::class.java)
}
