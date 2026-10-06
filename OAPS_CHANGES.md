# OAPS — personal build of AndroidAPS

This is a modified version of [AndroidAPS](https://github.com/nightscout/AndroidAPS),
based on AAPS **3.4.2.6** (upstream commit `598e2eb39c`, 2026-08-02).
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
| 4 | Browser build labelled OAPS, APKs `oaps-*.apk` in a Drive folder "OAPS" | `.github/workflows/branch-ci.yml` |
| 5 | Firebase config accepts the OAPS app ID | `app/google-services.json`, `wear/google-services.json` |
| 6 | Own launcher icon (orange loop around a drop) | `app/src/main/res/mipmap-*`, `wear/src/main/res/mipmap-*` |
| 7 | Conservative dosing defaults: SMB off, max IOB 0 until the user sets them | `core/keys/.../BooleanKey.kt`, `core/keys/.../DoubleKey.kt` |

## Change log

All times are Eastern Time (America/Toronto). Commit hashes refer to the
`oaps` branch of `sircsaba/AndroidAPS`.

### 2026-10-06 09:54 — Conservative dosing defaults (Trio-style)
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

### 2026-10-04 23:43 — Firebase config (`512f165b16`)
Added `info.nightscout.oaps` to `google-services.json` (app and wear); the
build fails without a matching entry. OAPS still points at the AAPS Firebase
project, so crash reports may go there or be dropped. Replace with your own
Firebase project, or remove Firebase, before sharing OAPS with others.

### 2026-10-04 23:25 — Browser build naming (`60cf297034`)
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
variant **fullRelease**. Uses the same keystore and Google Drive secrets as
your AAPS browser builds.

## Staying up to date with AndroidAPS

```
git remote add upstream https://github.com/nightscout/AndroidAPS.git
git fetch upstream
git merge upstream/master
```

Merge conflicts, if any, will be in the files listed in the summary table.
