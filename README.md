# Android Weather App

MVVM + Clean Architecture android application implemented from [
The Complete Android 14 & Kotlin Development Masterclass](https://www.udemy.com/course/android-kotlin-developer/)
course material project

## Overview

### The challenge

Users should be able to:

- See the screen contains current weather information as landing screen
- Can pull to refesh the current on landing screen
- Can swipe to navigate to weather forecast screen
- Uble to know screens that can swipe left and right
TBA
- Uble to search current weather info by location name
- Uble to search current weather forecast by location name
- See the air pollution info screen

## My process

### Built with

- Android KTX
- kotlinx-coroutines
- retrofit2
- Dexter
- Hilt
- play service loaction
- gson converter
- view pager 2
- swiperefreshlayout

and more dependencies and their version in [here](https://github.com/Chayapol-c/weather-app/blob/main/gradle/libs.versions.toml)

### Useful resource

- [Modeling Retrofit Responses With Sealed Classes and Coroutines](https://getstream.io/blog/modeling-retrofit-responses/) -
  this help me better handle retrofit response
- [Debugging Network Requests in Android with Chucker](https://engineering.teknasyon.com/debugging-network-requests-in-android-with-chucker-a-comprehensive-guide-9a1251c54e9e) -
  this help me integrate chucker with retrofit
- [รู้จักกับ ViewPager2 ที่จะมาแทน ViewPager แบบเดิมๆ](https://akexorcist.dev/view-pager-will-be-replace-with-view-pager-2/) - this help me implement view pager 2 with many fragment

### What I learned

- using play service
- implement use case
- ui event with kotlin flow
- view pager implementation
