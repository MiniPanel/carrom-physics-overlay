# Carrom Physics Overlay (Android)

This repository now contains a starter Android app that demonstrates a **draw-over-apps trajectory overlay** for a carrom board.

## What it does

- Requests `SYSTEM_ALERT_WINDOW` permission.
- Starts a full-screen overlay service (`TYPE_APPLICATION_OVERLAY`).
- Draws three editable points:
  - Striker position
  - Target coin position
  - Impact/contact point on the coin
- Computes and renders:
  - Incoming striker direction
  - Post-impact striker direction
  - Post-impact target coin direction
- Extends guide rays all the way to board boundaries (long enough to cover corner-pocket lines).

## Important technical note

Android overlays **cannot directly read another app's internal game state**. To make this work reliably in real gameplay, you need one of these:

1. A manual adjustment workflow (what this prototype does).
2. Screen capture + computer vision pipeline (`MediaProjection`) to detect striker/coin coordinates in real time.

## Running

1. Open project in Android Studio (Giraffe+ / AGP 8.1+).
2. Sync Gradle.
3. Run `app` on Android 10+ device.
4. Grant overlay permission.
5. Tap **Start trajectory overlay**.
6. On overlay:
   - Tap/drag to place current edit point.
   - Quick tap cycles edit mode: `striker -> coin -> impact`.

## Next improvements

- Auto-detect board + coins from screen frames.
- Account for wall bounces and friction.
- Add pocket-probability highlighting.
- Add calibration presets for different carrom game UIs.
