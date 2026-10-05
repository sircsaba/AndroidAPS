# OAPS — personal build of AndroidAPS

This is a personal fork of [AndroidAPS](https://github.com/nightscout/AndroidAPS).
It differs from upstream in three ways:

1. **Objectives removed.** `ObjectivesPlugin` no longer limits anything: loop,
   closed loop, autosens, SMB and automation are available immediately, and LGS
   mode is never forced by an unfinished objective. The Objectives tab is hidden
   and the setup wizard skips the objectives step.
   File: `plugins/constraints/.../objectives/ObjectivesPlugin.kt`

2. **No expiry lockout.** `VersionCheckerPlugin` no longer sets max IOB to 0 when
   the installed version is past its end date. The update check and the
   "new version available" / "version expired" notifications are kept.
   File: `plugins/constraints/.../versionChecker/VersionCheckerPlugin.kt`

3. **Separate app.** The full build uses app ID `info.nightscout.oaps` and the
   name "OAPS" (phone and watch), so it installs next to a normal AAPS instead
   of replacing it. It starts with its own empty settings and history.
   Never let AAPS and OAPS loop the same pump at the same time.

Unit tests for both plugins and `ConstraintsCheckerImplTest` were updated to match.

## Staying up to date

```
git remote add upstream https://github.com/nightscout/AndroidAPS.git
git fetch upstream
git merge upstream/master
```

Merge conflicts, if any, will be in the two files above.

## Warning

This build removes safety gates that AndroidAPS uses to make sure the user
understands the system before automated insulin delivery is enabled, and to
stop users running versions with known bugs. Use at your own risk.
