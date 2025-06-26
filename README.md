<a id="readme-top"></a>

# Lucky Wheel View

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

[![Android Weekly][android-weekly-shield]][android-weekly-url]
[![JitPack Version][jitpack-version-shield]][jitpack-url]

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  </br>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#compose-ui">Compose UI</a></li>
    <li><a href="#installation">Installation</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#examples">Examples</a></li>
    <li><a href="#customization">Customization</a></li>
    <li><a href="#support">Support</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->

## About The Project

There are many great Lucky Wheel View available on GitHub; however, I didn't find one that really
suited my needs so I created this enhanced one. I want to create a Lucky Wheel View so amazing that
it'll be the last one you ever need -- I think this is it.

Here's why:

* Almost all views/elements can be customize
* Almost no need for work on logic, all logic is settle
* Gradient/Solid color views/elements
* Nice and smooth animations
* Almost all events can listenable
* Random or specific target can be set
* Clockwise and counterclockwise rotate direction support
* Icon load from URL support with Coil
* Android Views and Compose UI support

Of course, your needs may be different. So I'll be adding more in the near future. You may also
suggest changes by forking this repo and creating a pull request or opening an issue. Thanks to all
the people have contributed to expanding this library!

Use the `Lucky Wheel View` to get started.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- COMPOSE UI -->

## Compose UI

`Lucky Wheel View` has Compose UI support. Check
<a href="https://github.com/caneryilmaz52/LuckyWheelViewCompose">Lucky Wheel View Compose</a> to use.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- INSTALLATION -->

## Installation

1. Add it in your root `build.gradle` at the end of repositories:

  ```gradle
 	allprojects {
 		repositories {
 			maven { url 'https://jitpack.io' }
 		}
 	}
  ```

or

```gradle
dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

2. Add the dependency

  ```gradle
 dependencies {
	        implementation 'com.github.caneryilmaz52:LuckyWheelView:LATEST_VERSION'
	}
  ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- USAGE -->

## Usage

```xml

<com.caneryilmaz.apps.luckywheel.ui.LuckyWheelView
        android:id="@+id/luckyWheelView"
        android:layout_width="350dp"
        android:layout_height="350dp" />
```

Populate a list of `WheelData`

`text` is wheel item text

`textColor` is color of item text

- if `textColor` size = 1 then gradient text color disable and text color will be value of `textColor[0]`
- if `textColor` size > 1 then gradient text color enable
- if `textColor` is empty then wheel view is not drawn

`backgroundColor` is background color of item

- if `backgroundColor` size = 1 then gradient background color disable and background color will be value of `backgroundColor[0]`
- if `backgroundColor` size > 1 then gradient background color enable
- if `backgroundColor` is empty then wheel view is not drawn

`textFontTypeface` is custom font typeface of item text

`icon` is item icon `Bitmap`, if not null then icon will be drawn

`iconURL` is item icon URL, if not null then icon will be drawn

- use only one of `icon` and `iconURL`
- if both are used, `iconURL` takes priority
- recommended to use PNG format icon

```kotlin
val wheelData = ArrayList<WheelData>()
val item = WheelData(
    text = itemText,
    textColor = intArrayOf(textColor),
    backgroundColor = intArrayOf(backgroundColor),
    textFontTypeface = itemTextFontTypeface,  //optional
    icon = itemIconBitmap, //optional
    iconURL = itemIconUrl //optional
)
wheelData.add(item)
```

Set data to `LuckyWheelView`

```kotlin
luckyWheelView.setWheelData(wheelData = wheelData)
```

Set winner target (default is 0)

```kotlin
luckyWheelView.setTarget(target = 3)
```

Set `RotationCompleteListener` listener to `LuckyWheelView`

```kotlin
luckyWheelView.setRotationCompleteListener { wheelData ->
    // do something with winner wheel data
  }
```

Set `RotationStatusListener` listener to `LuckyWheelView` if you need

```kotlin
luckyWheelView.setRotationStatusListener { status ->
    when (status) {
        RotationStatus.ROTATING -> { // do something
        }

        RotationStatus.IDLE -> { // do something
        }

        RotationStatus.COMPLETED -> { // do something
        }

        RotationStatus.CANCELED -> { // do something
        }
    }
}
```

Rotate wheel to target

```kotlin
luckyWheelView.rotateWheel()
```

<!-- EXAMPLES -->

## Examples

<img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif1.gif" width="250" height="250"/> <img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif2.gif" width="250" height="250"/> <img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif3.gif" width="250" height="250"/>

<img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif4.gif" width="250" height="250"/> <img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif5.gif" width="250" height="250"/> <img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif6.gif" width="250" height="250"/> 

<img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif7.gif" width="250" height="250"/> <img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/gif8.gif" width="250" height="250"/>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CUSTOMIZATION -->

## Customization

Get the perfect look with customization combinations.


<details>
  <summary>Arrow Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                                               | Default             | Description                                                                                                                                                                                                                                                                                                                  |
  |------------------------------------------------------------------------|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `setArrowPosition(arrowPosition: ArrowPosition)`                       | `ArrowPosition.TOP` | wheel arrow position `ArrowPosition.TOP` or `ArrowPosition.CENTER`                                                                                                                                                                                                                                                           |
  | `setArrowAnimationStatus(arrowAnimStatus: Boolean)`                    | `true`              | enable or disable arrow swing animation                                                                                                                                                                                                                                                                                      |
  | `setArrowSwingDuration(arrowSwingDuration: Int)`                       | `50ms`              | single arrow swing animation duration                                                                                                                                                                                                                                                                                        |
  | `setArrowSwingDistance(arrowSwingDistance: Float)`                     | `10F`               | arrow right and left swing distance                                                                                                                                                                                                                                                                                          |
  | `setArrowSwingSlowdownMultiplier(arrowSwingSlowdownMultiplier: Float)` | `0.1F`              | arrow swing animation duration slowdown speed <ul><li>The smaller the value, the later it slows down</li> <li>The larger the value, the faster it slows down</li></ul>                                                                                                                                                       |
  | `setWheelTopArrow(wheelArrowId: Int)`                                  | -                   | wheel top arrow drawable resource id                                                                                                                                                                                                                                                                                         |
  | `setWheelTopArrow(wheelArrowDrawable: Drawable)`                       | -                   | wheel top arrow drawable resource                                                                                                                                                                                                                                                                                            |
  | `setWheelTopArrowSize(width: Float, height: Float)`                    | `48dp`              | width and height of wheel top arrow image                                                                                                                                                                                                                                                                                    |
  | `setWheelTopArrowColor(wheelTopArrowColor: Int)`                       | -                   | wheel top arrow tint color                                                                                                                                                                                                                                                                                                   |
  | `setWheelTopArrowMargin(margin: Float)`                                | `0dp`               | wheel top arrow margin from bottom <ul><li>if value is positive then arrow moving up</li> <li>if value is negative then arrow moving down</li></ul>                                                                                                                                                                          |
  | `setWheelCenterArrow(wheelArrowId: Int)`                               | -                   | wheel center arrow drawable resource id                                                                                                                                                                                                                                                                                      |
  | `setWheelCenterArrow(wheelArrowDrawable: Drawable)`                    | -                   | wheel center arrow drawable resource                                                                                                                                                                                                                                                                                         |
  | `setWheelCenterArrowSize(width: Float, height: Float)`                 | `30dp`              | width and height of wheel center arrow image                                                                                                                                                                                                                                                                                 |
  | `setWheelCenterArrowColor(wheelCenterArrowColor: Int)`                 | -                   | wheel center arrow tint color                                                                                                                                                                                                                                                                                                |
  | `setWheelCenterArrowMargin(marginTop: Float, marginBottom: Float)`     | `0dp`               | wheel center arrow margin from top and bottom <ul><li>if `marginTop` value is positive then arrow moving down</li> <li>if `marginTop` value is negative then arrow moving up</li> <li>if `marginBottom` value is positive then arrow moving up</li> <li>if `marginBottom` value is negative then arrow moving down</li></ul> |

  </details>

- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute                      | Type        | Default             | Description                                                                                                                                                            |
  |--------------------------------|-------------|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `arrowPosition`                | `enum`      | `ArrowPosition.TOP` | wheel arrow position `ArrowPosition.TOP` or `ArrowPosition.CENTER`                                                                                                     |
  | `arrowAnimationEnable`         | `boolean`   | `true`              | enable or disable arrow swing animation                                                                                                                                |
  | `arrowSwingDuration`           | `integer`   | `50ms`              | single arrow swing animation duration                                                                                                                                  |
  | `arrowSwingDistance`           | `float`     | `10F`               | arrow right and left swing distance                                                                                                                                    |
  | `arrowSwingSlowdownMultiplier` | `float`     | `0.1F`              | arrow swing animation duration slowdown speed <ul><li>The smaller the value, the later it slows down</li> <li>The larger the value, the faster it slows down</li></ul> |
  | `wheelTopArrow`                | `drawable`  | -                   | wheel top arrow drawable resource id                                                                                                                                   |
  | `wheelTopArrowWidth`           | `dimension` | `48dp`              | width of wheel top arrow image                                                                                                                                         |
  | `wheelTopArrowHeight`          | `dimension` | `48dp`              | height of wheel top arrow image                                                                                                                                        |
  | `wheelTopArrowColor`           | `color`     | -                   | wheel top arrow tint color                                                                                                                                             |
  | `wheelTopArrowMargin`          | `dimension` | `0dp`               | wheel top arrow margin from bottom <ul><li>if value is positive then arrow moving up</li> <li>if value is negative then arrow moving down</li></ul>                    |
  | `wheelCenterArrow`             | `drawable`  | -                   | wheel center arrow drawable resource id                                                                                                                                |
  | `wheelCenterArrowWidth`        | `dimension` | `30dp`              | width of wheel center arrow image                                                                                                                                      |
  | `wheelCenterArrowHeight`       | `dimension` | `30dp`              | height of wheel center arrow image                                                                                                                                     |
  | `wheelCenterArrowColor`        | `color`     | -                   | wheel center arrow tint color                                                                                                                                          |
  | `wheelCenterArrowMarginTop`    | `dimension` | `0dp`               | wheel center arrow margin from top <ul><li>if value is positive then arrow moving down</li> <li>if value is negative then arrow moving up</li></ul>                    |
  | `wheelCenterArrowMarginBottom` | `dimension` | `0dp`               | wheel center arrow margin from bottom <ul><li>if value is positive then arrow moving up</li> <li>if value is negative then arrow moving down</li></ul>                 |

  </details>

</details>


<details>
  <summary>Wheel Center Text Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                                  | Default                   | Description                                                                                                                                                                                                                                                                                                                                                        |
  |-----------------------------------------------------------|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `setWheelCenterText(wheelCenterText: String)`             | -                         | center text value                                                                                                                                                                                                                                                                                                                                                  |
  | `setWheelCenterTextColor(wheelCenterTextColor: IntArray)` | `intArrayOf(Color.BLACK)` | color of center text <ul><li>if `wheelCenterTextColor` size = 1 then gradient text color disable and text color will be value of `wheelCenterTextColor[0]`</li> <li>if `wheelCenterTextColor` size > 1 then gradient text color enable</li> <li>if `wheelCenterTextColor` is empty then gradient text color disable and text color will be `Color.BLACK`</li></ul> |
  | `setWheelCenterTextSize(wheelCenterTextSize: Int)`        | `16sp`                    | size of center text                                                                                                                                                                                                                                                                                                                                                |
  | `setWheelCenterTextFont(fontResourceId: Int)`             | `Sans Serif`              | custom font resource id of center text                                                                                                                                                                                                                                                                                                                             |
  | `setWheelCenterTextFont(typeface: Typeface)`              | `Sans Serif`              | custom font typeface of center text                                                                                                                                                                                                                                                                                                                                |
  
  </details>
  
- <details>
  <summary>Attributes</summary>
  </br>

  | Attribute              | Type        | Default      | Description                            |
  |------------------------|-------------|--------------|----------------------------------------|
  | `wheelCenterText`      | `string`    | -            | center text value                      |
  | `wheelCenterTextColor` | `color`     | `#000000`    | color of center text                   |
  | `wheelCenterTextSize`  | `dimension` | `16sp`       | size of center text                    |
  | `wheelCenterTextFont`  | `reference` | `Sans Serif` | custom font resource id of center text |
  </details>

</details>

<details>
  <summary>Wheel Center Image Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                                  | Default | Description                             |
  |-----------------------------------------------------------|---------|-----------------------------------------|
  | `setWheelCenterImage(wheelCenterImageId: Int)`            | -       | wheel center image drawable resource id |
  | `setWheelCenterImage(wheelCenterImageDrawable: Drawable)` | -       | wheel center image drawable resource    |
  | `setWheelCenterImageSize(width: Float, height: Float)`    | `30dp`  | width and height of wheel center image  |

  </details>

- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute                | Type        | Default | Description                             |
  |--------------------------|-------------|---------|-----------------------------------------|
  | `wheelCenterImage`       | `drawable`  | -       | wheel center image drawable resource id |
  | `wheelCenterImageWidth`  | `dimension` | `30dp`  | width of wheel center image             |
  | `wheelCenterImageHeight` | `dimension` | `30dp`  | height of wheel center image            |

  </details>

</details>

<details>
  <summary>Wheel Rotation Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>

  | Function                                                   |  Default                      | Description                                                                                                                                                                                                                                                                                                                   |
  |------------------------------------------------------------|-------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `setTarget(target: Int)`                                   | `0`                           | index of the item to win <ul><li>`target` must be between 0 and wheelData last index (exclusive)</li> <li>if target a negative number then target throw `IllegalArgumentException`</li> <li>if target bigger than given array list last index then throw `IndexOutOfBoundsException`</li></ul>                                |
  | `setRotateRandomTarget(rotateRandomTarget: Boolean)`       | `false`                       | enable or disable rotate to random target                                                                                                                                                                                                                                                                                     |
  | `setRandomTargets(randomTargets: IntArray)`                | `intArrayOf()`                | array of win index <ul><li>if `rotateRandomTarget` is `true` and `randomTargets` is empty then win index will be randomly between `0` and `wheelData.latsIndex`</li> <li>if `rotateRandomTarget` is `true` and `randomTargets` is not empty then win index will be randomly one of members of `randomTargets` array</li></ul> |
  | `setRotateDirection(rotationDirection: RotationDirection)` | `RotationDirection.CLOCKWISE` | wheel rotate direction                                                                                                                                                                                                                                                                                                        |
  | `setRotationViaSwipe(rotationViaSwipe: Boolean)`           | `false`                       | enable or disable start wheel rotate via swipe down                                                                                                                                                                                                                                                                           |
  | `setSwipeDistance(swipeDistance: Int)`                     | `100`                         | swipe distance to start rotate wheel                                                                                                                                                                                                                                                                                          |
  | `stopCenterOfItem(stopCenterOfItem: Boolean)`              | `false`                       | <ul><li>if `true` the arrow points to the center of the slice</li> <li>if `false` the arrow points to a random point on the slice</li></ul>                                                                                                                                                                                   |
  | `setRotateTime(rotateTime: Long)`                          | `5000L`                       | wheel rotate duration                                                                                                                                                                                                                                                                                                         |
  | `setRotateSpeed(rotateSpeed: RotationSpeed)`               | `RotationSpeed.NORMAL`        | wheel rotate speed `RotationSpeed.FAST`, `RotationSpeed.NORMAL` or `RotationSpeed.SLOW`                                                                                                                                                                                                                                       |
  | `setRotateSpeedMultiplier(rotateSpeedMultiplier: Float)`   | `1F`                          | wheel rotate speed multiplier                                                                                                                                                                                                                                                                                                 |

  </details>
- <details>
  <summary>Attributes</summary>
  </br>

  | Attribute               | Type      | Default                       | Description                                                                                                                                 |
  |-------------------------|-----------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
  | `rotateDirection`       | `enum`    | `RotationDirection.CLOCKWISE` | wheel rotate direction                                                                                                                      |
  | `rotationViaSwipe`      | `boolean` | `false`                       | enable or disable start wheel rotate via swipe down                                                                                         |
  | `swipeDistance`         | `integer` | `100`                         | swipe distance to start rotate wheel                                                                                                        |
  | `stopCenterOfItem`      | `boolean` | `false`                       | <ul><li>if `true` the arrow points to the center of the slice</li> <li>if `false` the arrow points to a random point on the slice</li></ul> |
  | `rotateTime`            | `long`    | `5000L`                       | wheel rotate duration                                                                                                                       |
  | `rotateSpeed`           | `enum`    | `RotationSpeed.Normal`        | wheel rotate speed `RotationSpeed.FAST`, `RotationSpeed.NORMAL` or `RotationSpeed.SLOW`                                                     |
  | `rotateSpeedMultiplier` | `float`   | `1F`                          | wheel rotate speed multiplier                                                                                                               |

  </details>

</details>

<details>
  <summary>Wheel Stroke Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                               | Default                   | Description                                                                                                                                                                                                                                                                                                                                                  |
  |--------------------------------------------------------|---------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `drawWheelStroke(drawWheelStroke: Boolean)`            | `false`                   | enable or disable wheel corner stroke drawing                                                                                                                                                                                                                                                                                                                |
  | `setWheelStrokeColor(wheelStrokeColor: IntArray)`      | `intArrayOf(Color.BLACK)` | color of stroke line <ul><li>if `wheelStrokeColor` size = 1 then gradient stroke color disable and stroke color will be value of `wheelStrokeColor[0]`</li> <li>if `wheelStrokeColor` size > 1 then gradient stroke color enable</li> <li>if `wheelStrokeColor` is empty then gradient stroke color disable and stroke color will be `Color.BLACK`</li></ul> |
  | `setWheelStrokeThickness(wheelStrokeThickness: Float)` | `4dp`                     | thickness of item stroke circle                                                                                                                                                                                                                                                                                                                              |
  
  </details>

- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute              | Type        | Default   | Description                                   |
  |------------------------|-------------|-----------|-----------------------------------------------|
  | `drawWheelStroke`      | `boolean`   | `false`   | enable or disable wheel corner stroke drawing |
  | `wheelStrokeColor`     | `color`     | `#000000` | color of stroke line                          |
  | `wheelStrokeThickness` | `dimension` | `4dp`     | thickness of item stroke circle               |
 
 </details>

</details>

<details>
  <summary>Wheel Item Separator Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                                        | Default                   | Description                                                                                                                                                                                                                                                                                                                                                                                                     |
  |-----------------------------------------------------------------|---------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `drawItemSeparator(drawItemSeparator: Boolean)`                 | `false`                   | enable or disable wheel item separator drawing                                                                                                                                                                                                                                                                                                                                                                  |
  | `setWheelItemSeparatorColor(wheelItemSeparatorColor: IntArray)` | `intArrayOf(Color.BLACK)` | color of item separator line <ul><li>if `wheelItemSeparatorColor` size = 1 then gradient separator color disable and separator color will be value of `wheelItemSeparatorColor[0]`</li> <li>if `wheelItemSeparatorColor` size > 1 then gradient separator color enable</li> <li>if `wheelItemSeparatorColor` is empty then gradient separator color disable and separator color will be `Color.BLACK`</li></ul> |
  | `setItemSeparatorThickness`                                     | `2dp`                     | thickness of item separator line                                                                                                                                                                                                                                                                                                                                                                                |
  
  </details>
  
- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute                 | Type        | Default   | Description                                    |
  |---------------------------|-------------|-----------|------------------------------------------------|
  | `drawItemSeparator`       | `boolean`   | `false`   | enable or disable wheel item separator drawing |
  | `wheelItemSeparatorColor` | `color`     | `#000000` | color of item separator line                   |
  | `itemSeparatorThickness`  | `dimension` | `2dp`     | thickness of item separator line               |
  
  </details>

</details>

<details>
  <summary>Wheel Center Point Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                         | Default       | Description                            |
  |--------------------------------------------------|---------------|----------------------------------------|
  | `drawCenterPoint(drawCenterPoint: Boolean)`      | `false`       | enable or disable center point drawing |
  | `setCenterPointColor(centerPointColor: Int)`     | `Color.WHITE` | color of center point                  |
  | `setCenterPointRadius(centerPointRadius: Float)` | `20dp`        | radius of center point                 |
  
  </details>
- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute           | Type        | Default   | Description                            |
  |---------------------|-------------|-----------|----------------------------------------|
  | `drawCenterPoint`   | `boolean`   | `false`   | enable or disable center point drawing |
  | `centerPointColor`  | `color`     | `#FFFFFF` | color of center point                  |
  | `centerPointRadius` | `dimension` | `20dp`    | radius of center point                 |
  
  </details>

</details>

<details>
  <summary>Wheel Corner Points Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                                                 | Default        | Description                                                                                                                                                                                                                                                                                 |
  |--------------------------------------------------------------------------|----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `drawCornerPoints(drawCornerPoints: Boolean)`                            | `false`        | enable or disable corner points drawing                                                                                                                                                                                                                                                     |
  | `setCornerPointsEachSlice(cornerPointsEachSlice: Int)`                   | `1`            | count of point in a slice                                                                                                                                                                                                                                                                   |
  | `setCornerPointsColor(cornerPointsColor: IntArray)`                      | `intArrayOf()` | colors of corner points <ul><li>if `cornerPointsColor` is empty and `setUseRandomCornerPointsColor` is `false` then corner colors will be randomly</li> <li>if `cornerPointsColor` is not empty and `setUseRandomCornerPointsColor` is `true` then corner colors will be randomly</li></ul> |
  | `setUseRandomCornerPointsColor(useRandomCornerPointsColor: Boolean)`     | `true`         | enable or disable random corner points colors                                                                                                                                                                                                                                               |
  | `setUseCornerPointsGlowEffect(useCornerPointsGlowEffect: Boolean)`       | `true`         | enable or disable corner points glow effect                                                                                                                                                                                                                                                 |
  | `setCornerPointsColorChangeSpeedMs(cornerPointsColorChangeSpeedMs: Int)` | `500`          | corner points color change duration                                                                                                                                                                                                                                                         |
  | `setCornerPointsRadius(cornerPointsRadius: Float)`                       | `4dp`          | radius of corner point                                                                                                                                                                                                                                                                      |
 
  </details>
  
- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute                        | Type        | Default | Description                                   |
  |----------------------------------|-------------|---------|-----------------------------------------------|
  | `drawCornerPoints`               | `boolean`   | `false` | enable or disable corner points drawing       |
  | `cornerPointsEachSlice`          | `integer`   | `1`     | count of point in a slice                     |
  | `cornerPointsColor`              | `color`     | -       | color of corner points                        |
  | `useRandomCornerPointsColor`     | `boolean`   | `true`  | enable or disable random corner points colors |
  | `useCornerPointsGlowEffect`      | `boolean`   | `true`  | enable or disable corner points glow effect   |
  | `cornerPointsColorChangeSpeedMs` | `integer`   | `500`   | corner points color change duration           |
  | `cornerPointsRadius`             | `dimension` | `4dp`   | radius of corner point                        |

</details>

</details>

<details>
  <summary>Wheel Item Text Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                                                                 | Default                      | Description                                                                                                                                                                                                                                                                                  |
  |------------------------------------------------------------------------------------------|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `setTextOrientation(textOrientation: TextOrientation)`                                   | `TextOrientation.HORIZONTAL` | text orientation of wheel items `TextOrientation.HORIZONTAL`, `TextOrientation.VERTICAL`, `TextOrientation.VERTICAL_TO_CENTER` or `TextOrientation.VERTICAL_TO_CORNER`                                                                                                                       |
  | `setTextPadding(textPadding: Float)`                                                     | `4dp`                        | text padding from wheel corner                                                                                                                                                                                                                                                               |
  | `setTextSize(textSize: Int)`                                                             | `16sp`                       | text size of wheel items                                                                                                                                                                                                                                                                     |
  | `setTextLetterSpacing(@FloatRange(from = 0.0, to = 1.0) letterSpacing: Float)`           | `0.1F`                       | letter spacing of wheel items text <ul><li>letterSpacing must be in range `0.0F` - `1.0F`</li> <li>letterSpacing is not in range then letter spacing be `0.1F`</li></ul>                                                                                                                     |
  | `setTextFont(fontResourceId: Int)`                                                       | `Sans Serif`                 | custom font resource id of wheel items text                                                                                                                                                                                                                                                  |
  | `setTextFont(typeface: Typeface)`                                                        | `Sans Serif`                 | custom font typeface of wheel items text                                                                                                                                                                                                                                                     |
  | `setTextPositionFraction(@FloatRange(from = 0.1, to = 0.9) textPositionFraction: Float)` | `0.7F`                       | text vertical position fraction in wheel slice only effect when `TextOrientation` is `TextOrientation.VERTICAL_TO_CENTER` or `TextOrientation.VERTICAL_TO_CORNER` <ul><li>The smaller the value, the closer to the center</li> <li>The larger the value, the closer to the corners</li></ul> |
  
  </details>

- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute              | Type        | Default                      | Description                                                                                                                                                                                                                                                                                  |
  |------------------------|-------------|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `textOrientation`      | `enum`      | `TextOrientation.HORIZONTAL` | text orientation of wheel items `TextOrientation.HORIZONTAL`, `TextOrientation.VERTICAL`, `TextOrientation.VERTICAL_TO_CENTER` or `TextOrientation.VERTICAL_TO_CORNER`                                                                                                                       |
  | `textPadding`          | `dimension` | `4dp`                        | text padding from wheel corner                                                                                                                                                                                                                                                               |
  | `textSize`             | `dimension` | `16sp`                       | text size of wheel items                                                                                                                                                                                                                                                                     |
  | `letterSpacing`        | `float`     | `0.1F`                       | letter spacing of wheel items text <ul><li>letterSpacing must be in range `0.0F` - `1.0F`</li> <li>letterSpacing is not in range then letter spacing be `0.1F`</li></ul>                                                                                                                     |
  | `textFont`             | `reference` | `Sans Serif`                 | custom font resource id of wheel items text                                                                                                                                                                                                                                                  |
  | `textPositionFraction` | `float`     | `0.7F`                       | text vertical position fraction in wheel slice only effect when `TextOrientation` is `TextOrientation.VERTICAL_TO_CENTER` or `TextOrientation.VERTICAL_TO_CORNER` <ul><li>The smaller the value, the closer to the center</li> <li>The larger the value, the closer to the corners</li></ul> |
  
  </details>

</details>

<details>
  <summary>Wheel Item Icon Customization</summary>
  </br>

- <details>
  <summary>Functions</summary>
  </br>
  
  | Function                                                                         | Default | Description                                                                                                                                                               |
  |----------------------------------------------------------------------------------|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `setIconPosition(@FloatRange(from = 0.1, to = 0.9) iconPositionFraction: Float)` | `0.5F`  | icon vertical position fraction in wheel slice <ul><li>The smaller the value, the closer to the center</li> <li>The larger the value, the closer to the corners</li></ul> |
  | `setIconSizeMultiplier(sizeMultiplier: Float)`                                   | `1.0F`  | item icon size multiplier value, default icon size `36dp`                                                                                                                 |
  
  </details>

- <details>
  <summary>Attributes</summary>
  </br>
  
  | Attribute            | Type    | Default Value | Description                                                                                                                                                               |
  |----------------------|---------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `iconPosition`       | `float` | `0.5F`        | icon vertical position fraction in wheel slice <ul><li>The smaller the value, the closer to the center</li> <li>The larger the value, the closer to the corners</li></ul> |
  | `iconSizeMultiplier` | `float` | `1.0F`        | item icon size multiplier value, default icon size `36dp`                                                                                                                 |
  
  </details>

</details>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- Support -->

## Support

Having amazing people like you behind me is a huge motivation to keep pushing forward and improving
my work. 💪

If you like the work that I do, you can help and support me by buying a cup of coffee. ☕️

[!["Buy Me A Coffee"][buy-me-a-coffee-shield]][buy-me-a-coffee-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and
create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull
request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->

## License

Distributed under the [Apache 2.0 License](LICENSE). See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->

## Contact

<p align="center">
  <b>Made with ❤️ by Caner YILMAZ</b><br>
  <a href="mailto:caneryilmaz.apps@gmail.com">caneryilmaz.apps@gmail.com</a><br><br>
  <a>
    <img src="https://avatars.githubusercontent.com/u/32595397?s=400&u=2f3051570d7ad9d65304f34eba943012f380433d&v=4" width="200">
  </a>
</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/caneryilmaz52/LuckyWheelView?style=for-the-badge

[contributors-url]: https://github.com/caneryilmaz52/LuckyWheelView/graphs/contributors

[forks-shield]: https://img.shields.io/github/forks/caneryilmaz52/LuckyWheelView?style=for-the-badge

[forks-url]: https://github.com/caneryilmaz52/LuckyWheelView/network/members

[stars-shield]: https://img.shields.io/github/stars/caneryilmaz52/LuckyWheelView?style=for-the-badge

[stars-url]: https://github.com/caneryilmaz52/LuckyWheelView/stargazers

[issues-shield]: https://img.shields.io/github/issues/caneryilmaz52/LuckyWheelView?style=for-the-badge

[issues-url]: https://github.com/caneryilmaz52/LuckyWheelView/issues

[license-shield]: https://img.shields.io/github/license/caneryilmaz52/LuckyWheelView?style=for-the-badge

[license-url]: https://github.com/caneryilmaz52/LuckyWheelView/blob/master/LICENSE

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://linkedin.com/in/caneryilmaz52

[jitpack-url]: https://jitpack.io/#caneryilmaz52/LuckyWheelView

[jitpack-version-shield]: https://jitpack.io/v/caneryilmaz52/LuckyWheelView.svg

[jitpack-url]: https://jitpack.io/#caneryilmaz52/LuckyWheelView

[android-weekly-shield]: https://androidweekly.net/issues/issue-629/badge

[android-weekly-url]: https://androidweekly.net/issues/issue-629

[buy-me-a-coffee-shield]: https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png

[buy-me-a-coffee-url]: https://www.buymeacoffee.com/caneryilmaz
