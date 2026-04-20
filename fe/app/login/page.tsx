export default function LoginPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-2xl p-8 space-y-8">
        <div className="text-center">
          <h1 className="text-4xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600">
            Welcome Back
          </h1>
          <p className="text-gray-500 mt-2">Đăng nhập vào tài khoản của bạn</p>
        </div>
        <form className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700">Email hoặc Tên đăng nhập</label>
            <input type="text" className="mt-1 block w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none" placeholder="admin@ecommerce.com" />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Mật khẩu</label>
            <input type="password" placeholder="••••••••" className="mt-1 block w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none" />
          </div>
          <button type="submit" className="w-full bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white font-bold py-3 rounded-lg shadow-lg transform transition-transform hover:scale-[1.02]">
            Đăng Nhập
          </button>
        </form>
        <div className="text-center">
          <p className="text-sm text-gray-600">
            Chưa có tài khoản? <a href="#" className="text-blue-600 font-semibold hover:underline">Đăng ký ngay</a>
          </p>
        </div>
      </div>
    </div>
  );
}
