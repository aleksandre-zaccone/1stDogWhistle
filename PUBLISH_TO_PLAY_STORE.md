# How to Publish Dog Whistle to Google Play Store

## Before You Start — Save Your Keystore

Your signing keystore is required for every future app update. If you lose it, you can never update your app.

```
File:     dogwhistle-release.jks  (in project root)
Password: DogWhistle1st@2026
Alias:    dogwhistle
```

**Back this file up to a USB drive, Google Drive, or other safe location NOW.**

---

## Step 1 — Build the Release AAB

Every time you want to publish a new version, run this command from the project folder:

```bash
export JAVA_HOME=/home/aablotia/.local/share/JetBrains/Toolbox/apps/android-studio/jbr
export ANDROID_HOME=/home/aablotia/Devs/Android/SDK
export PATH=$PATH:$JAVA_HOME/bin:/usr/bin
bash gradlew bundleRelease
```

The output file will be at:
```
app/build/outputs/bundle/release/app-release.aab
```

---

## Step 2 — Create the App in Play Console

1. Go to [play.google.com/console](https://play.google.com/console)
2. Click **Create app** (top right)
3. Fill in:
   - **App name:** Dog Whistle
   - **Default language:** English (or your preferred language)
   - **App or game:** App
   - **Free or paid:** Free (or Paid if you want to charge)
4. Check both policy boxes
5. Click **Create app**

---

## Step 3 — Set Up the Store Listing

In the left menu go to **Store presence → Main store listing**

### Required fields:
- **Short description** (max 80 characters)
  > Example: *High-frequency dog whistle trainer for your pet.*
- **Full description** (max 4000 characters)
  > Example: *Dog Whistle generates precise high-frequency tones from 5kHz to 22kHz to help train your dog. Simple, distraction-free interface. Always consult your vet before use.*

### Required graphics:
| Asset | Size | Notes |
|-------|------|-------|
| App icon | 512 x 512 px | PNG, no alpha |
| Feature graphic | 1024 x 500 px | PNG or JPG |
| Phone screenshots | Min 2, max 8 | At least 320px on shortest side |

You can take screenshots directly from your phone:
- Open the app on your Pixel 9 Pro Fold
- Press **Power + Volume Down** to screenshot
- Transfer to PC and upload

---

## Step 4 — Complete Required Sections

In the left menu, complete each item marked with a red dot:

### App content (under Policy)
- **Privacy policy:** You need a URL to a privacy policy page. You can create a free one at [privacypolicygenerator.info](https://privacypolicygenerator.info)
- **Ads:** Select *No, my app does not contain ads*
- **Content rating:** Click **Start questionnaire** → choose *Utility* category → answer questions → Submit
- **Target audience:** Select the age group (likely 18+)
- **Data safety:** For this app — select *No* for all data collection (the app does not collect any user data)

---

## Step 5 — Upload the AAB and Create a Release

1. In the left menu go to **Release → Production**
2. Click **Create new release**
3. Under **App bundles**, click **Upload** and select:
   ```
   app/build/outputs/bundle/release/app-release.aab
   ```
4. In **Release notes**, write what's new:
   ```
   Initial release of Dog Whistle.
   ```
5. Click **Save** → **Review release** → **Start rollout to Production**

---

## Step 6 — Wait for Review

- Google reviews new apps within **1–3 business days** (sometimes faster)
- You will receive an email when approved or if there are issues
- Once approved, your app will be live on the Play Store within a few hours

---

## Updating the App in the Future

Every time you make changes and want to publish an update:

1. In `app/build.gradle.kts`, increase the version:
   ```kotlin
   versionCode = 2          // Must increase by at least 1 each time
   versionName = "1.1"      // Human-readable version shown in store
   ```
2. Build a new AAB (Step 1 above)
3. Go to Play Console → Production → Create new release → Upload new AAB

---

## What is Testing? Do You Need to Pay?

### Types of Testing in Play Console

Google Play offers three testing tracks before going to production. **None of them cost extra money** — they are free features included with your $25 developer account.

| Track | Who Can Access | Requires Payment? |
|-------|---------------|-------------------|
| **Internal testing** | Up to 100 specific people (by email) | No |
| **Closed testing (Alpha)** | Specific group of testers (by email or Google Group) | No |
| **Open testing (Beta)** | Anyone can join via a public opt-in link | No |
| **Production** | Everyone on the Play Store | No |

### Should You Use Testing?

**For a simple app like Dog Whistle — you can skip testing and go straight to Production.**

However, testing is useful if you want to:
- Let a few friends try the app before it goes public
- Check that the app works on different Android phones
- Get feedback before the official launch

### How Internal Testing Works (if you want to use it)

1. Go to **Release → Testing → Internal testing**
2. Click **Create new release** and upload your AAB
3. Go to **Testers** tab → Add tester email addresses
4. Share the opt-in link with your testers
5. They install the app and give you feedback
6. When ready, promote to Production

**Recommendation:** For your first release, just go straight to **Production**. If you want a few friends to test first, use **Internal testing** — it's free and takes 5 minutes to set up.

---

## Common Reasons Apps Get Rejected

- Missing privacy policy URL
- App crashes on launch (test on your phone first)
- Misleading app description
- Icon or screenshots don't meet size requirements
- Content rating not completed

---

## Quick Checklist Before Submitting

- [ ] App icon uploaded (512x512 PNG)
- [ ] Feature graphic uploaded (1024x500)
- [ ] At least 2 screenshots uploaded
- [ ] Short and full description filled in
- [ ] Privacy policy URL added
- [ ] Content rating questionnaire completed
- [ ] Data safety form completed
- [ ] AAB uploaded to Production release
- [ ] Release notes written
- [ ] Keystore file backed up safely
