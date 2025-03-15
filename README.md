## Wavy Slider

![Sliders](https://github.com/user-attachments/assets/19e21704-dc30-48a3-8d5f-1118dc9d4ee9)

To use the ready-made library, add the dependency:
```
dependencies {

    implementation("io.github.uratera:slider:1.0.1")
}
```

### Attributes
|Attributes    |Description   |Default value|
|--------------|--------------|-------------|
max            |Maximum value |100
min            |Minimum value |0
value          |Value         |0
thumbColor     |Thumb color   |purple-blue
thumbHeight    |Thumb height  |26dp
thumbIcon      |Thumb icon    |nothing
thumbIconColor |Thumb icon color |white
thumbRadius    |Thumb radius     |10dp
thumbShowHalo      |Thumb show halo    |true
thumbStroke        |Width stroke thumb |0
thumbStrokeColor   |Thumb stroke color |dark blue
thumbStyle         |Thumb style        |circular
thumbWidth         |Thumb width        |16dp
trackActiveColor   |Active track color |purple-blue
trackChangeColor   |Enable track color change |false
trackEndColor      |Track color at maximum value |red
trackHeight        |Track height         |8dp
trackInactiveColor |Inactive track color |blue-blue frost
trackStyle         |Track style |normal
waveCount          |Wave count  |10
waveSweep          |Wave sweep  |14dp
waveWidth          |Wave width  |3dp

**Styles of thumb:**
- circular
- round_rect - rect with rounded corners

**Styles of track:**
- normal - straight track
- wave – uniform wave
- stretch - stretching wave
- increase - increscent wave

**Usage:**
```
<com.tera.slider.SliderCustom
    android:id="@+id/slider"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```

**Value change listener:**

Kotlin
```
var myValue = 0f

slider.setOnChangeListener {
    myValue = it
}
```
Java
```
float myValue;

slider.setOnChangeListener(aFloat -> {
    myValue = aFloat;
    return null;
});
```

**Methods:**
```
setValueMax, getValueMax, setValue, getValue
```
