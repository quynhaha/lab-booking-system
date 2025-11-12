import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:go_router/go_router.dart';
import 'screens/home_screen.dart';
import 'screens/events_screen.dart';
import 'screens/bookings_screen.dart';
import 'screens/labs_screen.dart';
import 'screens/login_screen.dart';
import 'services/api_service.dart';
import 'services/lab_service.dart';
import 'services/booking_service.dart';
import 'providers/auth_provider.dart';

void main() {
  runApp(const LabBookingApp());
}

class LabBookingApp extends StatelessWidget {
  const LabBookingApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        // Auth Provider (ChangeNotifierProvider for state management)
        ChangeNotifierProvider(
          create: (_) => AuthProvider()..initialize(),
        ),
        
        // API Services (with token injection)
        ProxyProvider<AuthProvider, ApiService>(
          create: (_) => ApiService(),
          update: (_, auth, service) {
            service ??= ApiService();
            service.setToken(auth.token);
            return service;
          },
          dispose: (_, service) => service.dispose(),
        ),
        ProxyProvider<AuthProvider, LabService>(
          create: (_) => LabService(),
          update: (_, auth, service) {
            service ??= LabService();
            service.setToken(auth.token);
            return service;
          },
          dispose: (_, service) => service.dispose(),
        ),
        ProxyProvider<AuthProvider, BookingService>(
          create: (_) => BookingService(),
          update: (_, auth, service) {
            service ??= BookingService();
            service.setToken(auth.token);
            return service;
          },
          dispose: (_, service) => service.dispose(),
        ),
      ],
      child: Consumer<AuthProvider>(
        builder: (context, authProvider, child) {
          return MaterialApp.router(
            title: 'Quản lý phòng Lab',
            debugShowCheckedModeBanner: false,
            theme: ThemeData(
              colorScheme: ColorScheme.fromSeed(
                seedColor: const Color(0xFF1976D2), // Material Blue
                brightness: Brightness.light,
              ),
              useMaterial3: true,
              appBarTheme: const AppBarTheme(
                centerTitle: true,
                elevation: 0,
              ),
              cardTheme: const CardThemeData(
                elevation: 2,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                ),
              ),
            ),
            darkTheme: ThemeData(
              colorScheme: ColorScheme.fromSeed(
                seedColor: const Color(0xFF1976D2),
                brightness: Brightness.dark,
              ),
              useMaterial3: true,
              appBarTheme: const AppBarTheme(
                centerTitle: true,
                elevation: 0,
              ),
              cardTheme: const CardThemeData(
                elevation: 2,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(12)),
                ),
              ),
            ),
            routerConfig: _createRouter(authProvider),
          );
        },
      ),
    );
  }
}

GoRouter _createRouter(AuthProvider authProvider) {
  return GoRouter(
    refreshListenable: authProvider,
    redirect: (BuildContext context, GoRouterState state) {
      // Wait for auth initialization
      if (!authProvider.isInitialized) {
        return null; // Show splash/loading
      }

      final isAuthenticated = authProvider.isAuthenticated;
      final isLoginRoute = state.matchedLocation == '/login';

      // If not authenticated and not on login page, redirect to login
      if (!isAuthenticated && !isLoginRoute) {
        return '/login';
      }

      // If authenticated and on login page, redirect to home
      if (isAuthenticated && isLoginRoute) {
        return '/';
      }

      return null; // No redirect needed
    },
    routes: <RouteBase>[
      GoRoute(
        path: '/login',
        builder: (BuildContext context, GoRouterState state) {
          return const LoginScreen();
        },
      ),
      GoRoute(
        path: '/',
        builder: (BuildContext context, GoRouterState state) {
          return const HomeScreen();
        },
        routes: <RouteBase>[
          GoRoute(
            path: 'labs',
            builder: (BuildContext context, GoRouterState state) {
              return const LabsScreen();
            },
          ),
          GoRoute(
            path: 'events',
            builder: (BuildContext context, GoRouterState state) {
              return const EventsScreen();
            },
          ),
          GoRoute(
            path: 'bookings',
            builder: (BuildContext context, GoRouterState state) {
              return const BookingsScreen();
            },
          ),
        ],
      ),
    ],
  );
}