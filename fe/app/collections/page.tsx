export default function CollectionsPage() {
  const collections = [
    { name: 'Mùa Hè Rực Rỡ', count: '120+ Sản phẩm', color: 'from-orange-400 to-red-500' },
    { name: 'BST Thu Đông', count: '85+ Sản phẩm', color: 'from-blue-400 to-indigo-600' },
    { name: 'Phong Cách Đường Phố', count: '200+ Sản phẩm', color: 'from-green-400 to-teal-500' },
    { name: 'Đồ Công Sở Cao Cấp', count: '45+ Sản phẩm', color: 'from-purple-500 to-pink-500' },
  ];

  return (
    <div className="min-h-screen bg-gray-50 py-16 px-6">
      <div className="max-w-7xl mx-auto">
        <header className="mb-12">
          <h1 className="text-4xl font-bold text-gray-900">Bộ Sưu Tập</h1>
          <p className="text-gray-600 mt-2 text-lg">Khám phá tinh hoa thời trang qua từng thiết kế độc bản.</p>
        </header>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {collections.map((item, idx) => (
            <div key={idx} className={`relative h-80 rounded-2xl p-8 bg-gradient-to-br ${item.color} shadow-lg transform transition-all hover:scale-105 cursor-pointer group`}>
              <div className="flex flex-col h-full justify-between">
                <div>
                  <h3 className="text-2xl font-bold text-white mb-2">{item.name}</h3>
                  <p className="text-white text-opacity-80">{item.count}</p>
                </div>
                <div className="flex items-center text-white font-semibold">
                  <span>Khám phá ngay</span>
                  <svg className="w-5 h-5 ml-2 group-hover:translate-x-2 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                  </svg>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
