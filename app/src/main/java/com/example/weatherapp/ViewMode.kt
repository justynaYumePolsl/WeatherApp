import androidx.constraintlayout.motion.widget.Debug.getLocation

class ViewModeState(var currentMode:String) {
    fun changeMode(){
        currentMode = if(currentMode=="standard"){
            "elders"
        } else "standard"
    }

}