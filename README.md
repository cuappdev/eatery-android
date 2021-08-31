# Eatery for Android - Browse. Search. Eat.

<p align="center"><img src=https://raw.githubusercontent.com/cuappdev/assets/master/eatery/Eatery-Long-Logo.png width=500 /></p>

Eatery was the first app released by AppDev. With over 6,000 students using it every month, it enables students to browse menus and discover places to eat on Cornell’s campus and in Collegetown. It enhances the dining experience at Cornell with features such as providing the crowdedness of eateries, checking meal swipes and dining money balances, and favoriting dishes. Eatery is available on both [iOS](https://github.com/cuappdev/eatery-ios) and Android platforms.

[<img src="https://upload.wikimedia.org/wikipedia/commons/7/78/Google_Play_Store_badge_EN.svg" height="50">](https://play.google.com/store/apps/details?id=com.cornellappdev.android.eatery) &nbsp;&nbsp; [<img src="https://upload.wikimedia.org/wikipedia/commons/5/5d/Available_on_the_App_Store_%28black%29.png" height="50">](https://itunes.apple.com/us/app/eatery-cornell-dining-made/id1089672962?mt=8)

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:cuappdev/eatery-android.git
```

## Configuration
### Keystores:
Create `app/keystore.gradle` with the following info:
```gradle
ext.key_alias='...'
ext.key_password='...'
ext.store_password='...'
```
And place both keystores under `app/keystores/` directory:
- `playstore.keystore`


## Generating signed APK
From Android Studio:
1. ***Build*** menu
2. ***Generate Signed APK...***
3. Fill in the keystore information *(you only need to do this once manually and then let Android Studio remember it)*

## Maintainers
This project is maintained by the [Android Team @ Cornell Appdev](https://www.cornellappdev.com/team)

<details open>
<summary><b>Current Roster</b></summary>

- Kevin Sun (@kevinsun-dev)
- Connor Reinhold (@connorreinhold)
- Aastha Shah (@aasthashah999)
- Justin Jiang (@JiangoJ)
- Haichen Wang (@Haichen-Wang)
- Shiyuan Huang (@Shiyuan-Huang-23)
- Chris Desir (@ckdesir)
- Adam Kadhim (@hockeymonday)
- Junyu Wang (@JessieWang0706)
- Corwin Zhang (@Corfish123)

</details>

<details open>
<summary><b>Alumni</b></summary>

- Jae Choi (@jyc979)
- Abdullah Islam (@abdullah248)
- Lesley Huang (@ningning621)
- Jehron Petty (@JehronPett)
- Jonvi Rollins (@djr277)
- Preston Rozwood (@Pdbz199)
- Joseph Fulgieri (@jmf373)

</details>

<details open>
<summary><b>Special Thanks</b></summary>

- Evan Welsh (@ewlsh)
- Yanlam Ko (@YKo20010)
- Young Kim (@young-k)
- Ziwei Gu (@ZiweiGu)
- Austin Astorga (@AAAstorga)

 </details>


## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Run the linter: https://developer.android.com/studio/write/lint
5. Push your branch (git push origin my-new-feature)
6. Create a new Pull Request