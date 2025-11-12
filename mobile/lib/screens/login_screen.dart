import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import 'register_screen.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _obscurePassword = true;

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  Future<void> _handleLogin() async {
    if (!_formKey.currentState!.validate()) return;

    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    authProvider.clearError();

    final success = await authProvider.loginWithFptSso(
      email: _emailController.text.trim(),
      password: _passwordController.text,
    );

    if (success && mounted) {
      // Navigation will be handled by the router based on auth state
      // No need to manually navigate
    }
  }

  Future<void> _handleGoogleLogin() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    authProvider.clearError();

    final success = await authProvider.loginWithGoogle();

    if (success && mounted) {
      // Navigation will be handled by the router based on auth state
      // No need to manually navigate
    } else if (mounted && authProvider.error != null) {
      // Show error message if login failed
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(authProvider.error ?? 'Đăng nhập Google thất bại'),
          backgroundColor: Colors.red,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              Color(0xFFFF6C00), // FPT Orange
              Color(0xFFFFA500),
              Color(0xFFFFFFFF), // White
            ],
          ),
        ),
        child: SafeArea(
          child: Center(
            child: SingleChildScrollView(
              padding: const EdgeInsets.all(24.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // FPT Logo
                  Container(
                    padding: const EdgeInsets.all(20),
                    decoration: BoxDecoration(
                      color: Colors.white,
                      borderRadius: BorderRadius.circular(20),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.1),
                          blurRadius: 20,
                          offset: const Offset(0, 10),
                        ),
                      ],
                    ),
                    child: Image.network(
                      'https://iconape.com/wp-content/png_logo_vector/fpt-university-logo.png',
                      height: 80,
                      errorBuilder: (context, error, stackTrace) {
                        return const Icon(
                          Icons.school_rounded,
                          size: 80,
                          color: Color(0xFFFF6C00),
                        );
                      },
                    ),
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'Lab Booking',
                    style: theme.textTheme.headlineMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: Colors.white,
                      shadows: [
                        Shadow(
                          color: Colors.black.withOpacity(0.3),
                          offset: const Offset(0, 2),
                          blurRadius: 4,
                        ),
                      ],
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Đăng nhập để đặt phòng lab',
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: Colors.white,
                      shadows: [
                        Shadow(
                          color: Colors.black.withOpacity(0.3),
                          offset: const Offset(0, 1),
                          blurRadius: 2,
                        ),
                      ],
                    ),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 48),

                  // Login Form Card
                  Card(
                    elevation: 8,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20),
                    ),
                    child: Padding(
                      padding: const EdgeInsets.all(24.0),
                      child: Form(
                        key: _formKey,
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            // Email Field
                            TextFormField(
                              controller: _emailController,
                              keyboardType: TextInputType.emailAddress,
                              decoration: InputDecoration(
                                labelText: 'Email FPT',
                                hintText: 'example@fpt.edu.vn',
                                prefixIcon: const Icon(Icons.email_outlined),
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                ),
                              ),
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return 'Vui lòng nhập email';
                                }
                                if (!value.contains('@')) {
                                  return 'Email không hợp lệ';
                                }
                                if (!value.endsWith('@fpt.edu.vn')) {
                                  return 'Chỉ email @fpt.edu.vn được phép';
                                }
                                return null;
                              },
                            ),
                            const SizedBox(height: 16),

                            // Password Field
                            TextFormField(
                              controller: _passwordController,
                              obscureText: _obscurePassword,
                              decoration: InputDecoration(
                                labelText: 'Mật khẩu',
                                hintText: 'Nhập mật khẩu',
                                prefixIcon: const Icon(Icons.lock_outline),
                                suffixIcon: IconButton(
                                  icon: Icon(
                                    _obscurePassword
                                        ? Icons.visibility_outlined
                                        : Icons.visibility_off_outlined,
                                  ),
                                  onPressed: () {
                                    setState(() {
                                      _obscurePassword = !_obscurePassword;
                                    });
                                  },
                                ),
                                border: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(12),
                                ),
                              ),
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return 'Vui lòng nhập mật khẩu';
                                }
                                if (value.length < 6) {
                                  return 'Mật khẩu phải có ít nhất 6 ký tự';
                                }
                                return null;
                              },
                            ),
                            const SizedBox(height: 24),

                            // Error Message
                            Consumer<AuthProvider>(
                              builder: (context, authProvider, child) {
                                if (authProvider.error != null) {
                                  return Padding(
                                    padding: const EdgeInsets.only(bottom: 16),
                                    child: Text(
                                      authProvider.error!,
                                      style: TextStyle(
                                        color: theme.colorScheme.error,
                                        fontSize: 14,
                                      ),
                                      textAlign: TextAlign.center,
                                    ),
                                  );
                                }
                                return const SizedBox.shrink();
                              },
                            ),

                            // Login Button
                            Consumer<AuthProvider>(
                              builder: (context, authProvider, child) {
                                return FilledButton(
                                  onPressed: authProvider.isLoading
                                      ? null
                                      : _handleLogin,
                                  style: FilledButton.styleFrom(
                                    padding: const EdgeInsets.symmetric(
                                      vertical: 16,
                                    ),
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                  ),
                                  child: authProvider.isLoading
                                      ? const SizedBox(
                                          height: 20,
                                          width: 20,
                                          child: CircularProgressIndicator(
                                            strokeWidth: 2,
                                          ),
                                        )
                                      : const Text(
                                          'Đăng nhập',
                                          style: TextStyle(
                                            fontSize: 16,
                                            fontWeight: FontWeight.bold,
                                          ),
                                        ),
                                );
                              },
                            ),
                            const SizedBox(height: 16),

                            // Divider
                            Row(
                              children: [
                                Expanded(child: Divider(color: Colors.grey.shade300)),
                                Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 16),
                                  child: Text(
                                    'hoặc',
                                    style: theme.textTheme.bodySmall?.copyWith(
                                      color: Colors.grey.shade600,
                                    ),
                                  ),
                                ),
                                Expanded(child: Divider(color: Colors.grey.shade300)),
                              ],
                            ),
                            const SizedBox(height: 16),

                            // Google Sign In Button
                            Consumer<AuthProvider>(
                              builder: (context, authProvider, child) {
                                return OutlinedButton.icon(
                                  onPressed: authProvider.isLoading
                                      ? null
                                      : _handleGoogleLogin,
                                  style: OutlinedButton.styleFrom(
                                    padding: const EdgeInsets.symmetric(
                                      vertical: 16,
                                    ),
                                    side: BorderSide(
                                      color: Colors.grey.shade300,
                                      width: 1.5,
                                    ),
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                  ),
                                  icon: Image.network(
                                    'https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg',
                                    height: 24,
                                    width: 24,
                                    errorBuilder: (context, error, stackTrace) {
                                      return const Icon(
                                        Icons.g_mobiledata,
                                        size: 24,
                                        color: Colors.blue,
                                      );
                                    },
                                  ),
                                  label: const Text(
                                    'Đăng nhập bằng Google',
                                    style: TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.w600,
                                      color: Colors.black87,
                                    ),
                                  ),
                                );
                              },
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                  const SizedBox(height: 24),

                  // Register Link
                  Container(
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.9),
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text(
                          'Chưa có tài khoản? ',
                          style: theme.textTheme.bodyMedium,
                        ),
                        TextButton(
                          onPressed: () {
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (context) => const RegisterScreen(),
                              ),
                            );
                          },
                          child: const Text(
                            'Đăng ký ngay',
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                              color: Color(0xFFFF6C00),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
