import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.spotifyclone.R

class SwipeGestureDetector(context: Context,private val view: View ,private val listener: OnSwipeListener) :
    View.OnTouchListener {

    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val diffX = e2?.x?.minus(e1?.x!!) ?: 0f
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {

                    listener.onSwipeRight()
                    animateRight(view)
                } else {
                    listener.onSwipeLeft()
                    animateLeft(view)
                }
                return true
            }
            return false
        }
    }

    interface OnSwipeListener {
        fun onSwipeRight()
        fun onSwipeLeft()
    }

    private fun animateRight(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_out)
        view.startAnimation(animation)
    }

    private fun animateLeft(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_in)
        view.startAnimation(animation)
    }
}
