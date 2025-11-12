import '../models/event.dart';

/// Helper class to temporarily store selected event when navigating from Events screen to Labs screen
/// This allows teacher to select an event, then navigate to labs, and book a lab for that event
class EventSelectionHelper {
  static Event? _selectedEventForBooking;
  
  static void setSelectedEvent(Event? event) {
    _selectedEventForBooking = event;
  }
  
  static Event? getSelectedEvent() {
    return _selectedEventForBooking;
  }
  
  static void clearSelectedEvent() {
    _selectedEventForBooking = null;
  }
}

