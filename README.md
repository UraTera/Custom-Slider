## Wavy Slider
Open source.

![Sliders](https://github.com/user-attachments/assets/f7fcaf45-b2a2-473c-80d6-c26bcbae2b43)

To use the ready-made library, add the dependency:
```
dependencies {

    implementation("io.github.uratera:slider:1.0.0")
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

### Styles
|Thumb styles |Description|
|-------------|-----------|
circular      |Circular
round_rect    |Rect with rounded corners


|Track styles |Description|
|-------------|-----------|
normal        |Straight track 
wave          |Uniform wave
stretch       |Stretching wave
increase      |Increscent wave

