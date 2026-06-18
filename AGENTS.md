# Project Blueprint & QA Test Directives

The user provided an extensive plan to elevate the Freelance AI application to production standards. This document tracks the structural requirements over 7 phases:

## Phase 1: Architecture and Data Layer
- **Firebase Authentication**: Use standard OAuth2 flows or Firebase Auth logic if credentials are provided (otherwise mock auth flow intelligently).
- **Local Persistence (Room)**: 
  - Store User Profiles
  - Store Projects and Tasks
  - Store Invoices
  - Persist AI Chat history.

## Phase 2: Critical Bug Fixes
- **Invoice Builder**: Dynamically calculate totals, safeguard inputs, implement PDF exports in the future, and save generated invoices.
- **AI Chat**: Maintain session and persist history using Room.
- **Network Error Handling**: Replace raw exceptions with Snackbars or error states.
- **Project Manager**: Use MVVM/StateFlow instead of placeholders. Enable task creation and status changes.
- **Dashboard Buttons**: Fix any dead endpoints.

## Phase 3: Missing Features
- Dedicated AI tools: Proposal Writer, Content Writer, Email Writer, Code Gen, Resume Gen.
- Add Time Tracker, Payment Calculator, Contract Templates.

## Phase 4: Performance Optimization
- Optimize app startup speed, memory usage, recompositions, list performances.

## Phase 5: Security 
- Secure API keys (ensure GEMINI_API_KEY is not exposed directly). Utilize BuildConfig.
- Standard sanitizations.

## Phase 6: Responsive Design
- Add `WindowWidthSizeClass` configurations eventually to support mobile/tablet/desktop.

## Phase 7: Code Quality
- Enforce strict Clean MVVM + Repository pattern with Coroutines/StateFlow.
