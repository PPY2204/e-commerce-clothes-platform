export default function AboutPage() {
  return (
    <div className="min-h-screen bg-white">
      <div className="relative h-[400px] bg-gray-900 flex items-center justify-center text-center overflow-hidden">
        <div className="absolute inset-0 bg-opacity-60 bg-black z-10"></div>
        <div className="relative z-20 px-4">
          <h1 className="text-5xl font-bold text-white mb-4">Câu chuyện của chúng tôi</h1>
          <p className="text-xl text-gray-300 max-w-2xl mx-auto">Kiến tạo phong cách thời trang bền vững và đẳng cấp cho thế hệ mới.</p>
        </div>
      </div>
      <div className="max-w-4xl mx-auto py-20 px-6 prose prose-lg">
        <h2 className="text-3xl font-bold text-gray-900 mb-6">Tầm nhìn</h2>
        <p className="text-gray-600 leading-relaxed mb-8">
          Chúng tôi tin rằng thời trang không chỉ là vẻ bề ngoài, mà còn là cách chúng ta thể hiện bản sắc cá nhân và trách nhiệm với cộng đồng.
        </p>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 text-center mt-12">
          <div className="p-6 bg-gray-50 rounded-xl">
            <h3 className="text-2xl font-bold text-blue-600 mb-2">10K+</h3>
            <p className="text-gray-500">Khách hàng tin dùng</p>
          </div>
          <div className="p-6 bg-gray-50 rounded-xl">
            <h3 className="text-2xl font-bold text-blue-600 mb-2">50+</h3>
            <p className="text-gray-500">Đối tác toàn cầu</p>
          </div>
          <div className="p-6 bg-gray-50 rounded-xl">
            <h3 className="text-2xl font-bold text-blue-600 mb-2">100%</h3>
            <p className="text-gray-500">Chất lượng cam kết</p>
          </div>
        </div>
      </div>
    </div>
  );
}
