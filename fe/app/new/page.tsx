export default function NewArrivalsPage() {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
      <div className="max-w-4xl w-full text-center space-y-12">
        <div>
          <span className="bg-blue-100 text-blue-600 px-4 py-2 rounded-full text-sm font-bold uppercase tracking-wider">Mùa Xuân 2026</span>
          <h1 className="text-6xl font-black text-gray-900 mt-6 mb-4">Sản Phẩm Mới Nhất</h1>
          <p className="text-xl text-gray-500">Những thiết kế vừa ra mắt, dẫn đầu xu hướng năm nay.</p>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[1, 2, 3].map((id) => (
            <div key={id} className="h-96 bg-white rounded-2xl shadow-sm border border-gray-100 animate-pulse flex flex-col p-4">
              <div className="flex-1 bg-gray-100 rounded-xl mb-4"></div>
              <div className="h-4 w-3/4 bg-gray-100 rounded mb-2"></div>
              <div className="h-4 w-1/2 bg-gray-100 rounded"></div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
