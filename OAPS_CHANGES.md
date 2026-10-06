# OAPS — personal build of AndroidAPS

This is a modified version of [AndroidAPS](https://github.com/nightscout/AndroidAPS),
based on AAPS **3.4.2.6** (upstream commit `598e2eb39c`, 2026-08-02).
It contains no Firebase and needs no Google account to build.
Like AndroidAPS, it is licensed under the **GNU AGPL-3.0** (see `LICENSE.txt`)
and comes with **no warranty of any kind**.

> **Warning.** OAPS doses insulin automatically. It removes the AAPS
> objectives and the version-expiry lockout. You are fully responsible for
> your own settings and safety. Build it only for yourself, and only if you
> already understand closed-loop dosing.

## Summary of differences from AndroidAPS

| # | Change | Main files |
|---|--------|-----------|
| 1 | Objectives no longer restrict anything | `plugins/constraints/.../objectives/ObjectivesPlugin.kt` |
| 2 | Expired versions are no longer locked (max IOB not forced to 0) | `plugins/constraints/.../versionChecker/VersionCheckerPlugin.kt` |
| 3 | Separate app: ID `info.nightscout.oaps`, name "OAPS" | `app/build.gradle.kts`, `wear/build.gradle.kts` |
| 4 | Browser build labelled OAPS; APKs `oaps-*.apk` attached to the GitHub run (no Google Drive) | `.github/workflows/branch-ci.yml` |
| 5 | Firebase removed entirely (no analytics, crash reports or remote config) | see 2026-10-06 18:53 entry |
| 6 | Own launcher icon (orange loop around a drop) | `app/src/main/res/mipmap-*`, `wear/src/main/res/mipmap-*` |
| 7 | Conservative dosing defaults: SMB off, max IOB 0 until the user sets them | `core/keys/.../BooleanKey.kt`, `core/keys/.../DoubleKey.kt` |

## Change log

All times are Eastern Time (America/Toronto). Commit hashes refer to the
`oaps` branch of `sircsaba/AndroidAPS`.

### 2026-10-06 18:53 — Firebase and Google Drive removed
**Firebase (Analytics, Crashlytics, Remote Config, Installations) is gone.**
OAPS sends no usage data or crash reports anywhere; everything is only written
to the local log on the phone.
- Deleted `app/google-services.json` and `wear/google-services.json`; removed
  the `google-services` and `firebase-crashlytics` Gradle plugins and the
  Firebase libraries (`build.gradle.kts`, `app/`, `core/utils/`, `plugins/automation/`).
- `FabricPrivacyImpl`: now logs locally only (no Firebase calls).
- `MainActivity`: removed Crashlytics custom keys (version, hash, email…).
- `MainApp`: removed Firebase Remote Config. The version checker uses the
  version definitions bundled in the app instead of AAPS's online list.
- `InstanceId`: random local ID per app start instead of a Firebase ID
  (used only as the Virtual Pump serial and in the survey screen).
- `TriggerLocation` (automation): the Google Maps key came from Firebase, so
  the map picker in location triggers will not show a map; enter coordinates
  or use "current location" instead. Location triggers themselves still work.
- EOPatch driver: `Preconditions` now from Guava (previously came from Google
  Play Services through Firebase; identical behaviour).
- `plugins/sync`: declares `kotlinx-coroutines-play-services` directly (it
  used to come through Firebase; needed for the watch connection).
- Google Play Services **Wearable** and **Location** are kept: they are needed
  for the Wear OS watch and for location-based automation. They are not Firebase.

**Browser build no longer uses Google Drive.** The `GDRIVE_OAUTH2` secret is no
longer needed or read. APKs are attached to the GitHub Actions run as a
download ("Artifacts") and are deleted automatically after 3 days. Only the
keystore secrets are still required.

### 2026-10-06 09:54 — Conservative dosing defaults (Trio-style) (`f161ac2360`)
Without objectives, AAPS's own defaults (SMB on, max IOB 3 U, or
auto-calculated in simple mode) would apply from the first day on a fresh
install. OAPS now starts as cautiously as Trio/iAPS:
- **Enable SMB** (`use_smb`) defaults to **off**, and is no longer forced on
  by simple mode. The switch is visible and user-controlled in every mode.
- **Max IOB** for SMB (`openapsmb_max_iob`) and AMA (`openapsma_max_iob`)
  default to **0 U**, and are no longer auto-calculated in simple mode.
  The value is always exactly what the user enters, in every mode.
- With max IOB at 0 the loop can only reduce or suspend basal (low-glucose
  suspend behaviour) until the user deliberately raises max IOB.
- Values already saved by the user (including settings imported from an AAPS
  export) are kept. Only installs with no saved value get the new defaults.
- The SMB sub-options (SMB always, with COB, after carbs, etc.) are unchanged;
  they only take effect once SMB is switched on.

### 2026-10-05 08:56 — Own launcher icon (`666e22042a`)
Adaptive orange "loop around a drop" icon for the phone, round version for
Wear OS. Other build flavors keep the AAPS icons. In-app and notification
icons are still the AAPS ones.

### 2026-10-04 23:43 — Firebase config (`512f165b16`) — superseded 2026-10-06 18:53
Added `info.nightscout.oaps` to `google-services.json` (app and wear); the
build fails without a matching entry. OAPS still points at the AAPS Firebase
project, so crash reports may go there or be dropped. Replace with your own
Firebase project, or remove Firebase, before sharing OAPS with others.

### 2026-10-04 23:25 — Browser build naming (`60cf297034`) — Drive upload superseded 2026-10-06 18:53
Branch CI job is named "Build OAPS", APKs are `oaps-<version>.apk` /
`oaps-wear-<version>.apk`, uploaded to a Google Drive folder "OAPS", so they
never mix with regular AAPS builds.

### 2026-10-04 22:44 — Separate app (`4c84a83287`)
Full flavor (phone and watch) uses application ID `info.nightscout.oaps` and
app name "OAPS", so it installs next to a normal AAPS with its own settings
and history. **Never let AAPS and OAPS control the same pump at the same time.**

### 2026-10-04 22:21 — Objectives and version lockout removed (`2b05e625d0`)
- `ObjectivesPlugin`: loop, closed loop, autosens, SMB and automation are no
  longer gated; LGS is never forced; all objectives report as accomplished;
  the Objectives tab is hidden and the setup wizard skips it.
- `VersionCheckerPlugin`: an expired version no longer sets max IOB to 0. The
  update check and the "new version" / "version expired" notifications stay.
- Unit tests updated (`ObjectivesPluginTest`, `VersionCheckerPluginTest`,
  `ConstraintsCheckerImplTest`).

## Building

GitHub → Actions → **Branch CI** → Run workflow → branch **oaps** →
variant **fullRelease**. When it finishes, open the run and download the
APKs from the **Artifacts** section at the bottom (kept 3 days).
Only the keystore secrets are needed (`KEYSTORE_SET`, or `KEYSTORE_BASE64`,
`KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`).

## Staying up to date with AndroidAPS

```
git remote add upstream https://github.com/nightscout/AndroidAPS.git
git fetch upstream
git merge upstream/master
```

Merge conflicts, if any, will be in the files listed in the summary table.
