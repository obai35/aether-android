# Aether Freelancer - Android Companion App

A native Android companion app for the Aether AI Agent System's freelancer automation platform. Monitor, control, and interact with autonomous freelance job execution from your mobile device.

## Features

### Real-time Job Monitoring
- **Live job status** via WebSocket connection to backend
- **Progress tracking** through all stages: planning, coding, testing, quality gate, delivery
- **Timeline view** of all automation events per job

### Human-in-the-Loop Actions
- **Push notifications** for approval requests, quality gate failures, and delivery confirmations
- **One-tap approval/rejection** of proposals
- **Biometric authentication** for secure access

### AI Assistant Integration
- **Conversational interface** to query job status, start missions, export deliverables
- **Tool calling** for automated actions (get job status, trigger missions, export packages)

### Auto Mission Control
- **Configure and launch** autonomous freelancer missions from mobile
- **Platform selection** (RemoteOK, Mostaql, Khamsat, Freelancer.com, etc.)
- **Skill matching** with real-time filtering
- **Quality thresholds** and auto-delivery options

### Deliverable Management
- **Export packages** (English/Arabic RTL-ready)
- **Quality gate reports** with blocking issues
- **Direct download** of ZIP deliverables

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Android Companion App                        │
├─────────────────────────────────────────────────────────────────┤
│  UI Layer (Jetpack Compose)                                     │
│  ├── DashboardScreen     - Stats, recent jobs, quick actions   │
│  ├── JobsScreen          - Filterable job list                 │
│  ├── JobDetailScreen     - Overview, progress, quality, events │
│  ├── AssistantScreen     - AI chat with tool calling           │
│  ├── AutomationScreen    - Configure auto missions             │
│  └── SettingsScreen      - Backend URL, API key, notifications │
├─────────────────────────────────────────────────────────────────┤
│  ViewModel Layer (Hilt + StateFlow)                             │
│  └── FreelancerViewModel - State management, business logic    │
├─────────────────────────────────────────────────────────────────┤
│  Repository Layer                                               │
│  └── FreelancerRepository - API + WebSocket + Cache            │
├─────────────────────────────────────────────────────────────────┤
│  Data Layer                                                     │
│  ├── AetherApiService    - Retrofit REST client                │
│  ├── AetherWebSocketClient - OkHttp WebSocket for real-time   │
│  ├── Room Database       - Local job cache                     │
│  ├── DataStore           - Preferences (API key, settings)     │
│  └── EncryptedSharedPrefs - Secure API key storage             │
├─────────────────────────────────────────────────────────────────┤
│  Platform Services                                              │
│  ├── FCM Service         - Push notifications                  │
│  ├── Biometric Auth      - Fingerprint/Face unlock             │
│  └── WorkManager         - Background sync                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                    HTTPS + WSS
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Aether Backend (FastAPI)                     │
├─────────────────────────────────────────────────────────────────┤
│  REST Endpoints                                                 │
│  ├── GET  /api/freelancer/jobs           - List jobs           │
│  ├── GET  /api/freelancer/jobs/{id}      - Job details         │
│  ├── POST /api/freelancer/jobs/{id}/approve                     │
│  ├── POST /api/freelancer/jobs/{id}/deliver                     │
│  ├── GET  /api/freelancer/export/{id}    - English package     │
│  ├── GET  /api/freelancer/export_arabic/{id} - Arabic package  │
│  ├── POST /api/freelancer/auto_mission   - Start mission       │
│  ├── GET  /api/freelancer/stats          - Dashboard stats     │
│  └── POST /api/assistant/chat            - AI assistant        │
├─────────────────────────────────────────────────────────────────┤
│  WebSocket Endpoints                                            │
│  ├── /ws/agents        - Agent status + automation events      │
│  └── /ws/logs          - System logs                            │
├─────────────────────────────────────────────────────────────────┤
│  Event System                                                   │
│  ├── freelancer_execute_job  - Emits stage_changed, progress   │
│  ├── freelancer_auto_mission - Emits mission events            │
│  └── Broadcasts to all connected WebSocket clients             │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

### Job Status Updates (Real-time)
```
Backend (freelancer_execute_job)
    │
    ├── _emit_freelancer_event("stage_changed", job_id, {...})
    │
    ▼
WebSocket (/ws/agents)
    │
    ▼
Android WebSocket Client
    │
    ▼
FreelancerRepository.onEventReceived()
    │
    ▼
FreelancerViewModel (StateFlow update)
    │
    ▼
UI (Recompose with new state)
```

### Human Action Required
```
Backend (quality gate fails / needs approval)
    │
    ├── _emit_freelancer_event("human_required", job_id, {...})
    │
    ▼
WebSocket → Android
    │
    ▼
FreelancerViewModel.pendingHumanAction = event
    │
    ▼
UI shows HumanActionBanner
    │
    ▼
User taps "Review" → Navigate to JobDetailScreen
    │
    ▼
User taps "Approve" → POST /api/freelancer/jobs/{id}/approve
    │
    ▼
Backend updates status → emits "job_completed" event
```

### Push Notifications (FCM)
```
Backend detects human_required event
    │
    ├── Sends FCM message to user's device token
    │
    ▼
FirebaseMessagingService.onMessageReceived()
    │
    ▼
Creates high-priority notification
    │
    ▼
User taps notification → MainActivity with job_id extra
    │
    ▼
NavController navigates to JobDetailScreen(job_id)
```

## Setup

### Prerequisites
- Android Studio Ladybug (2024.2.1) or later
- JDK 17+
- Aether backend running (see backend README)

### Configuration

1. **Update API Base URL** in `NetworkModule.kt`:
```kotlin
val baseUrl = "https://aether-backend.onrender.com/"
val wsUrl = "wss://aether-backend.onrender.com"
```

2. **Firebase Configuration**:
   - Add `google-services.json` to `app/`
   - Enable Firebase Cloud Messaging

3. **Build and Run**:
```bash
cd android
./gradlew assembleDebug
```

### Backend Requirements

The backend must have these endpoints available (added to `main.py`):
- `GET /api/freelancer/jobs` - List all jobs
- `GET /api/freelancer/jobs/{id}` - Job details
- `POST /api/freelancer/jobs/{id}/approve` - Approve proposal
- `POST /api/freelancer/jobs/{id}/deliver` - Confirm delivery
- `GET /api/freelancer/export/{id}` - Export English package
- `GET /api/freelancer/export_arabic/{id}` - Export Arabic package
- `POST /api/freelancer/auto_mission` - Trigger auto mission
- `GET /api/freelancer/stats` - Dashboard statistics
- `POST /api/assistant/chat` - AI assistant endpoint
- `WS /ws/agents` - Real-time automation events
- `WS /ws/logs` - System logs

## Security

- **API Key Storage**: EncryptedSharedPreferences with Android Keystore
- **Biometric Auth**: Required on app launch (configurable)
- **Network Security**: HTTPS + WSS only (cleartext disabled)
- **Certificate Pinning**: Recommended for production

## Event Types (WebSocket)

| Event Type | Description | Data Payload |
|------------|-------------|--------------|
| `job_started` | Job execution begun | `{stage, message}` |
| `stage_changed` | Pipeline stage transition | `{stage, message, attempt?}` |
| `quality_gate_started` | Quality checks running | `{message}` |
| `quality_gate_completed` | Quality gate result | `{passed, checks, blocking_issues}` |
| `human_required` | Action needed from user | `{reason, action_required, context}` |
| `job_completed` | Job finished successfully | `{job, deliverable_path}` |
| `job_failed` | Job failed | `{error}` |
| `new_job_found` | Auto mission found job | `{job, match_score}` |
| `proposal_generated` | Proposal created | `{job_id, proposal}` |
| `deliverable_ready` | Package exported | `{job_id, package_path}` |
| `mission_started` | Auto mission begun | `{platforms, query}` |
| `mission_completed` | Auto mission done | `{executed, delivered}` |

## Human Action Types

| Action | Description | Required UI |
|--------|-------------|-------------|
| `approve_proposal` | Review and approve job proposal | Show proposal, Approve/Reject buttons |
| `review_code` | Manual code review needed | Show code diff, Accept/Request Changes |
| `provide_credentials` | API keys/secrets needed | Secure input form |
| `confirm_delivery` | Final delivery confirmation | Show deliverable summary, Confirm |
| `resolve_error` | Fix execution error | Show error details, Retry/Abort |

## Extending the App

### Adding New Screens
1. Create `@Composable` in `ui/screens/`
2. Add route to `MainActivity.kt` NavHost
3. Add navigation from existing screens

### Adding New API Endpoints
1. Add method to `AetherApiService` interface
2. Add model classes in `data/model/`
3. Add repository method in `FreelancerRepository`
4. Expose via `FreelancerViewModel`

### Adding New Event Types
1. Add to `AutomationEvent.EventType` enum
2. Add corresponding `EventData` sealed class
3. Handle in `FreelancerViewModel.observeEvents()`
4. Add UI for new event type

## Testing

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# UI tests
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.aether.companion.ui.DashboardScreenTest
```

## Building for Release

```bash
# Generate signed bundle
./gradlew bundleRelease

# Or APK
./gradlew assembleRelease
```

## License

Part of the Aether AI Agent System. See root LICENSE file.# Force rebuild
