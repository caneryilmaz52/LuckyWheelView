<a id="readme-top"></a>

# Lucky Wheel View

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

[![JitPack Version][jitpack-version-shield]][linkedin-url]
[![JitPack Download Week][jitpack-download-week-shield]][linkedin-url]
[![JitPack Download Month][jitpack-download-month-shield]][linkedin-url]

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#installation">Installation</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#examples">Examples</a></li>
    <li><a href="#customization">Customization</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

There are many great Lucky Wheel View available on GitHub; however, I didn't find one that really suited my needs so I created this enhanced one. I want to create a Lucky Wheel View so amazing that it'll be the last one you ever need -- I think this is it.

Here's why:
* Almost all views/elements can be customize
* Almost all events can listenable
* Random or specific target can be set
* Almost no need for work on logic, all logic is settle

Of course, your needs may be different. So I'll be adding more in the near future. You may also suggest changes by forking this repo and creating a pull request or opening an issue. Thanks to all the people have contributed to expanding this library!

Use the `Lucky Wheel View` to get started.

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
	        implementation 'com.github.caneryilmaz52:LuckyWheelView:v1.0'
	}
  ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- USAGE -->
## Usage

```xml
<com.caneryilmaz.apps.luckywheel.ui.LuckyWheelView
        android:id="@+id/luckyWheelView"
        android:layout_width="300dp"
        android:layout_height="300dp" />
```

Populate a list of `WheelData`

```kotlin
val wheelData = ArrayList<WheelData>()
val item = WheelData(
                text = itemText,
                textColor = textColor,
                backgroundColor = backgroundColor
            )
```

Set data to `LuckyWheelView`

```kotlin
luckyWheelView.setWheelData(wheelData = wheelData)
```

Set winner target (default is 0)

```kotlin
luckyWheelView.setTarget(
            target = 3
            rotateRandomTarget = false
        )
```

Set `TargetReachListener` listener to `LuckyWheelView`

```kotlin
luckyWheelView.setTargetReachListener { wheelData ->
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

<a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%201.png" width="200"></a>  <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%202.png" width="200"></a>  <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%203.png" width="200"></a> <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%204.png" width="200"></a> <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%205.png" width="200"></a> <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%206.png" width="200"></a> <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%207.png" width="200"></a> <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%208.png" width="200"></a> <a><img src="https://github.com/caneryilmaz52/LuckyWheelView/blob/main/images/wheel%209.png" width="200"></a>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CUSTOMIZATION -->
## Customization

Get the perfect look with customization combinations.

<details>
  <summary>Background Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`setRootLayoutBackgroundDrawable` | set a drawable to lucky wheel view background | `drawable` | `null`
`setRootLayoutBackgroundColor` | set a color to lucky wheel view background | `color` | `transparent`
 `setRootLayoutPadding` | set padding of lucky wheel view | `dimension` | `5dp`
</details>

<details>
  <summary>Arrow Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`setArrowPosition` | set arrow position to top or center | `enum` | `ArrowPosition.TOP`
`setArrowAnimationStatus` | set arrow animation status | `boolean` | `true`
`setArrowAnimation` | set arrow animation resource | `reference` | Shake animation
`setWheelTopArrow` | set top arrow image resource | `drawable` | -
`setWheelTopArrowSize` | set top arrow size | `dimension` | `48dp`
`setWheelTopArrowColor` | set top arrow color | `color` | -
`setWheelTopArrowMargin` | set top arrow margin | `dimension` | `0dp`
`setWheelCenterArrow` | set center arrow image resource | `drawable` | -
`setWheelCenterArrowSize` | set center arrow size | `dimension` | `30dp`
`setWheelCenterArrowColor` | set center arrow color | `color` | -
`setWheelCenterArrowMargin` | set center arrow margin | `dimension` | `0dp`

</details>

<details>
  <summary>Wheel Center Text Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`setWheelCenterText` | set a text to lucky wheel view center | `string` | -
`setWheelCenterTextColor` | set center text color | `color` | `#000000`
`setWheelCenterTextSize` | set center text size | `dimension` | `16sp`
`setWheelCenterTextFont` | set text font of center text | `typeface` | `Sans Serif`
`setWheelCenterTextFont` | set text font resource of center text | `reference` | `Sans Serif`

</details>

<details>
  <summary>Wheel Rotation Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`setTarget` | set winner item position | `integer` | `0`
`setRotateRandomTarget` | set random winner target | `boolean` | `false`
`setRotationViaSwipe` | set rotate wheel via user swipe | `boolean` | `false`
`setSwipeDistance` | set rotate wheel via swipe distance | `integer` | `100`
`stopCenterOfItem` | set wheel stop position center or random position of wheel slice | `boolean` | `false`
`setRotateTime` | set wheel rotation time in ms | `long` | `5000L`
`setRotateSpeed` | set wheel rotation speed | `enum` | `RotationSpeed.Normal`
`setRotateSpeedMultiplier` | set wheel rotation speed multiplier | `float` | `1F`

</details>

<details>
  <summary>Wheel Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`setWheelColor` | set wheel color | `color` | `#FFFFFF`
`setWheelPadding` | set wheel padding | `dimension` | `2dp`

</details>

<details>
  <summary>Wheel Item Separator Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`drawItemSeparator` | set draw item separator | `boolean` | `false`
`setWheelItemSeparatorColor` | set item separator color | `color` | `#000000`
`setItemSeparatorThickness` | set item separator thickness | `float` | `1F`

</details>

<details>
  <summary>Wheel Center Point Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`drawCenterPoint` | set draw a point center of wheel | `boolean` | `false`
`setCenterPointColor` | set center point color | `color` | `#FFFFFF`
`setCenterPointRadius` | set center point size | `float` | `30F`

</details>

<details>
  <summary>Wheel Item Text Customization</summary>
  </br>
  
 Function Name | Description | Type | Default Value
--- | --- | --- | --- 
`setTextOrientation` | set wheel item text orientation | `enum` | `TextOrientation.HORIZONTAL`
`setTextPadding` | set wheel item text padding | `dimension` | `20dp`
`setTextSize` | set wheel item text size | `dimension` | `16sp`
`setTextLetterSpacing` | set wheel item text letter spacing between 0.1F - 1.0F | `float` | `0.1F`
`setTextFont` | set text font of center text | `typeface` | `Sans Serif`
`setTextFont` | set text font resource of center text | `reference` | `Sans Serif`

</details>


<!-- ROADMAP -->
## Roadmap

- [ ] Attributes development
- [ ] Arrow animation slowdown development
- [ ] Any won effect (vibrate, confetti etc) when wheel stop
- [ ] Light effect like carnival zone
- [ ] Gradient item background


See the [open issues](https://github.com/othneildrew/Best-README-Template/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
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
[jitpack-download-week-shield]: https://jitpack.io/v/caneryilmaz52/LuckyWheelView/week.svg
[jitpack-download-month-shield]: https://jitpack.io/v/caneryilmaz52/LuckyWheelView/month.svg
