import React, { useState, useEffect } from 'react';
import axios from 'axios';

// --- Kiểu dữ liệu tương thích với Backend ---
interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

// [FR-28] Kiểu cấu trúc gói dịch vụ từ ServicePackage.java
interface ServicePackage {
  id: number;
  name: string;
  imageLimit: number;
  price: number;
  description: string;
  durationDays: number;
}

// [FR-27] Cấu trúc thống kê từ Clinic.java và ClinicRepository.java
interface ClinicStats {
  id: number;
  name: string;
  totalAnalyzed: number;
  currentPackageLimit: number;
  highRiskPatientsCount: number;
  expiryDate: string | null;           // Nhận chuỗi ngày giờ từ LocalDateTime Backend
  currentPackage: ServicePackage | null; // Tham chiếu đến ServicePackage Entity
}

const BASE_URL = "http://localhost:8080/api";

const ClinicDashboard: React.FC = () => {
  // --- Quản lý Trạng thái ---
  const [activeTab, setActiveTab] = useState<'dashboard' | 'doctors' | 'upload'>('dashboard');
  const [stats, setStats] = useState<ClinicStats | null>(null);
  const [userList, setUserList] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  
  // State quản lý form thêm nhân sự
  const [isOpenModal, setIsOpenModal] = useState<boolean>(false);
  const [formData, setFormData] = useState({ name: '', email: '', password: '', role: 'DOCTOR' });

  // [FR-28] State quản lý danh sách và giao diện gói dịch vụ
  const [packages, setPackages] = useState<ServicePackage[]>([]);
  const [isOpenUpgradeModal, setIsOpenUpgradeModal] = useState<boolean>(false);

  const clinicId = 1; // Giả lập mã ID phòng khám hiện tại

  // --- 1. Lấy thông tin thống kê hạn mức của phòng khám [FR-27] ---
  const fetchDashboardStats = async () => {
    try {
      const response = await axios.get(`${BASE_URL}/v1/billing/stats/${clinicId}`);
      setStats(response.data);
    } catch (error) {
      console.error("Lỗi khi tải dữ liệu thống kê hạn mức phòng khám:", error);
    }
  };

  // --- 2. Lấy danh sách gói dịch vụ mở để hiển thị nâng cấp [FR-28] ---
  const fetchAvailablePackages = async () => {
    try {
      const response = await axios.get(`${BASE_URL}/v1/billing/packages`);
      setPackages(response.data);
    } catch (error) {
      console.error("Lỗi khi tải danh sách gói dịch vụ từ hệ thống:", error);
    }
  };

  // --- 3. Lấy danh sách nhân viên y tế từ UserController.java ---
  const fetchUsers = async () => {
    setIsLoading(true);
    try {
      const response = await axios.get(`${BASE_URL}/users/search?name=`);
      setUserList(response.data);
    } catch (error) {
      console.error("Lỗi khi tải danh sách người dùng:", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardStats();
    fetchUsers();
    fetchAvailablePackages();
  }, []);

  // --- 4. Tạo tài khoản nhân sự mới ---
  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await axios.post(`${BASE_URL}/users/register`, formData);
      alert("Đăng ký tài khoản nhân viên thành công!");
      setIsOpenModal(false);
      setFormData({ name: '', email: '', password: '', role: 'DOCTOR' });
      fetchUsers();
    } catch (error) {
      alert("Không thể đăng ký tài khoản nhân viên. Vui lòng kiểm tra lại.");
    }
  };

  // --- 5. Tải ảnh lên và theo dõi trừ hạn mức [FR-27] ---
  const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const files = event.target.files;
    if (!files || files.length === 0) return;

    try {
      // Gửi số lượng tệp lên API track-usage của BillingController.java
      await axios.post(`${BASE_URL}/v1/billing/track-usage/${clinicId}?count=${files.length}`);
      alert(`Đã xử lý và phân tích thành công ${files.length} tệp ảnh võng mạc!`);
      fetchDashboardStats(); // Làm mới thanh tiến trình lập tức
    } catch (error: any) {
      if (error.response && error.response.status === 403) {
        alert("Từ chối xử lý: " + (error.response.data || "Vượt quá giới hạn gói dịch vụ!"));
      } else {
        alert("Lỗi hệ thống khi cập nhật dung lượng xử lý ảnh.");
      }
    }
  };

  // --- 6. Thực hiện mua hoặc nâng cấp gói dịch vụ [FR-28] ---
  const handleUpgradePackage = async (packageId: number, packageName: string) => {
    const confirmAction = window.confirm(`Xác nhận thực hiện nâng cấp/gia hạn gói dịch vụ sang: [${packageName}]?`);
    if (!confirmAction) return;

    try {
      // Truyền RequestBody dạng JSON khớp với API backend
      await axios.post(`${BASE_URL}/v1/billing/upgrade`, {
        clinicId: clinicId,
        packageId: packageId
      });
      alert(`Kích hoạt thành công gói dịch vụ [${packageName}]!`);
      setIsOpenUpgradeModal(false);
      fetchDashboardStats(); // Tải lại hạn mức mới hiển thị lên màn hình
    } catch (error: any) {
      alert("Nâng cấp thất bại: " + (error.response?.data || "Đã xảy ra lỗi hệ thống."));
    }
  };

  // --- Tính toán phần trăm thanh tiến trình tiêu thụ ảnh ---
  const totalAnalyzed = stats?.totalAnalyzed || 0;
  const currentLimit = stats?.currentPackageLimit || 1;
  const usagePercentage = Math.min(Math.round((totalAnalyzed / currentLimit) * 100), 100);

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col font-sans text-gray-800 antialiased">
      {/* HEADER BAR */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-40 px-8 py-4 flex justify-between items-center shadow-sm">
        <div className="flex items-center space-x-3">
          <div className="bg-blue-600 p-2 rounded-xl text-white font-black text-xl tracking-wider shadow-md">AI</div>
          <div>
            <h2 className="text-lg font-black text-gray-800 tracking-tight">{stats?.name || "Tên Phòng Khám"}</h2>
            <p className="text-xs text-gray-400 font-medium">Hệ thống phân tích hình ảnh võng mạc</p>
          </div>
        </div>
        
        {/* Nút nâng cấp gói */}
        <div className="flex items-center space-x-4">
          <button 
            onClick={() => setIsOpenUpgradeModal(true)}
            className="bg-gradient-to-r from-amber-500 to-orange-600 text-white font-bold text-xs px-4 py-2.5 rounded-xl shadow-md hover:opacity-90 transition-all">
            ⭐ Nâng cấp / Gia hạn Gói
          </button>
          <div className="h-8 w-px bg-gray-200"></div>
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold text-sm">AD</div>
            <span className="text-sm font-semibold text-gray-600">Quản trị viên</span>
          </div>
        </div>
      </header>

      {/* NỘI DUNG CHÍNH */}
      <div className="flex flex-1 max-w-[1600px] w-full mx-auto p-6 gap-6">
        {/* SIDEBAR */}
        <aside className="w-64 bg-white rounded-2xl p-4 border border-gray-200 shadow-sm flex flex-col justify-between h-[fit-content]">
          <nav className="space-y-1">
            <button onClick={() => setActiveTab('dashboard')} className={`w-full flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${activeTab === 'dashboard' ? 'bg-blue-50 text-blue-600' : 'text-gray-500 hover:bg-gray-50'}`}>
              <span>📊 Tổng quan thống kê</span>
            </button>
            <button onClick={() => setActiveTab('doctors')} className={`w-full flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${activeTab === 'doctors' ? 'bg-blue-50 text-blue-600' : 'text-gray-500 hover:bg-gray-50'}`}>
              <span>🧑‍⚕️ Quản lý nhân sự</span>
            </button>
            <button onClick={() => setActiveTab('upload')} className={`w-full flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-bold transition-all ${activeTab === 'upload' ? 'bg-blue-50 text-blue-600' : 'text-gray-500 hover:bg-gray-50'}`}>
              <span>📁 Tải ảnh võng mạc</span>
            </button>
          </nav>
          
          {/* Box hiển thị gói hiện tại */}
          <div className="mt-8 pt-4 border-t border-gray-100 px-2">
            <div className="bg-slate-50 rounded-xl p-3 border border-slate-200">
              <span className="text-[10px] font-bold text-gray-400 uppercase tracking-wider block">Gói hiện hành</span>
              <span className="text-xs font-black text-blue-950 mt-0.5 block">
                {stats?.currentPackage?.name || "Gói Mặc Định"}
              </span>
              <span className="text-[10px] text-gray-500 mt-1 block">
                Hạn dùng: {stats?.expiryDate ? new Date(stats.expiryDate).toLocaleDateString('vi-VN') : 'Vô thời hạn'}
              </span>
            </div>
          </div>
        </aside>

        {/* KHU VỰC HIỂN THỊ CHÍNH */}
        <main className="flex-1 flex flex-col">
          
          {/* TAB 1: TỔNG QUAN THỐNG KÊ (activeTab === 'dashboard') */}
          {activeTab === 'dashboard' && (
            <div className="space-y-6">
              {/* THẺ CHỈ SỐ DASHBOARD */}
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                
                {/* [FR-27] PROGRESS BAR THEO DÕI SỐ LƯỢNG ẢNH ĐÃ PHÂN TÍCH */}
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200 flex flex-col justify-between">
                  <div>
                    <span className="text-xs font-bold text-gray-400 uppercase block mb-1">Hạn mức ảnh đã xử lý</span>
                    <div className="flex items-baseline space-x-1">
                      <span className="text-3xl font-black text-gray-800">{totalAnalyzed}</span>
                      <span className="text-sm text-gray-400 font-medium">/ {stats?.currentPackageLimit} lượt ảnh</span>
                    </div>
                  </div>
                  
                  {/* Thanh Tiến trình trực quan */}
                  <div className="mt-4">
                    <div className="w-full bg-gray-100 rounded-full h-2.5 overflow-hidden border">
                      <div 
                        className={`h-full rounded-full transition-all duration-500 ${usagePercentage > 85 ? 'bg-rose-500' : 'bg-blue-600'}`} 
                        style={{ width: `${usagePercentage}%` }}>
                      </div>
                    </div>
                    <div className="flex justify-between items-center mt-2 text-[11px] font-semibold text-gray-500">
                      <span>Đã dùng {usagePercentage}%</span>
                      <span>Còn {Math.max((stats?.currentPackageLimit || 0) - totalAnalyzed, 0)} lượt</span>
                    </div>
                  </div>
                </div>

                {/* THẺ THỐNG KÊ CA BỆNH NGUY CƠ CAO */}
                <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-200 flex flex-col justify-between">
                  <div>
                    <span className="text-xs font-bold text-gray-400 uppercase block mb-1">Ca bệnh nguy cơ cao</span>
                    <span className="text-3xl font-black text-rose-600">{stats?.highRiskPatientsCount || 0}</span>
                  </div>
                  <p className="text-xs text-gray-400 font-medium mt-4">Hệ thống AI đề xuất bác sĩ ưu tiên xem trước.</p>
                </div>

                {/* CARD NHANH ĐI ĐẾN MODULE TẢI ẢNH */}
                <div className="bg-gradient-to-br from-blue-600 to-indigo-700 p-6 rounded-2xl shadow-md text-white flex flex-col justify-between">
                  <div>
                    <h3 className="text-base font-black tracking-tight">Phân tích hình ảnh</h3>
                    <p className="text-xs text-blue-100/80 mt-1 leading-relaxed">Tải ảnh võng mạc để tiến hành nhận diện bệnh lý tự động thông qua lõi xử lý AI.</p>
                  </div>
                  <button onClick={() => setActiveTab('upload')} className="mt-4 bg-white/10 hover:bg-white/20 text-white text-xs font-bold py-2 px-4 rounded-xl text-center w-full">
                    Bắt đầu tải ảnh &rarr;
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* TAB 2: QUẢN LÝ NHÂN SỰ (activeTab === 'doctors') */}
          {activeTab === 'doctors' && (
            <div className="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
              <div className="p-6 border-b border-gray-100 flex justify-between items-center">
                <div>
                  <h3 className="text-base font-black text-gray-800">Danh sách nhân sự</h3>
                  <p className="text-xs text-gray-400 font-medium mt-0.5">Danh sách tài khoản Bác sĩ và Quản trị viên</p>
                </div>
                <button onClick={() => setIsOpenModal(true)} className="bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs px-4 py-2.5 rounded-xl shadow-md transition-colors">
                  + Thêm nhân sự
                </button>
              </div>

              {isLoading ? (
                <div className="p-12 text-center text-sm font-semibold text-gray-400">Đang đồng bộ dữ liệu...</div>
              ) : (
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse">
                    <thead>
                      <tr className="bg-gray-50 border-b text-[11px] font-bold text-gray-400 uppercase">
                        <th className="px-6 py-3.5">Họ và tên</th>
                        <th className="px-6 py-3.5">Tài khoản Email</th>
                        <th className="px-6 py-3.5">Vai trò</th>
                        <th className="px-6 py-3.5">Trạng thái</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100 text-sm font-medium text-gray-600">
                      {userList.map((user) => (
                        <tr key={user.id} className="hover:bg-slate-50/50">
                          <td className="px-6 py-4 font-bold text-gray-700">{user.name}</td>
                          <td className="px-6 py-4 text-gray-500">{user.email}</td>
                          <td className="px-6 py-4">
                            <span className={`text-[10px] font-extrabold px-2 py-1 rounded-md ${user.role === 'ADMIN' ? 'bg-purple-50 text-purple-600' : 'bg-blue-50 text-blue-600'}`}>
                              {user.role}
                            </span>
                          </td>
                          <td className="px-6 py-4">
                            <span className="flex items-center space-x-1.5 text-xs text-emerald-600 font-semibold">
                              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
                              <span>Hoạt động</span>
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {/* TAB 3: TẢI ẢNH VÒNG MẠC (activeTab === 'upload') */}
          {activeTab === 'upload' && (
            <div className="bg-white p-10 rounded-2xl shadow-sm border border-gray-200 flex-1 flex flex-col items-center justify-center text-center max-w-3xl mx-auto w-full my-8">
              <div className="bg-blue-50 w-16 h-16 rounded-2xl flex items-center justify-center text-blue-600 text-2xl mb-4 shadow-inner">📁</div>
              <p className="text-lg font-black text-gray-800 mb-1">Kéo thả tệp hoặc chọn ảnh chụp đáy mắt</p>
              <p className="text-xs text-gray-400 font-medium max-w-sm mb-6 leading-relaxed">Mỗi ảnh tải lên thành công hệ thống sẽ tự động đối soát và khấu trừ trực tiếp vào tổng số lượng ảnh được cấp phép của gói.</p>
              
              <label className="cursor-pointer bg-blue-600 hover:bg-blue-700 text-white font-bold text-sm px-6 py-3 rounded-xl shadow-md block transition-colors">
                Chọn ảnh phân tích
                <input type="file" multiple className="hidden" onChange={handleFileUpload} />
              </label>
            </div>
          )}
        </main>
      </div>

      {/* POPUP: CẤP TÀI KHOẢN NHÂN VIÊN */}
      {isOpenModal && (
        <div className="fixed inset-0 bg-gray-900/40 backdrop-blur-sm flex justify-center items-center z-50 p-4">
          <form onSubmit={handleCreateUser} className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-gray-100">
            <div className="flex justify-between items-center pb-4 border-b border-gray-100 mb-5">
              <h3 className="text-base font-black text-gray-800">Cấp tài khoản nhân sự mới</h3>
              <button type="button" onClick={() => setIsOpenModal(false)} className="text-gray-400 hover:text-gray-600 text-xl font-bold">&times;</button>
            </div>
            
            <div className="space-y-4">
              <div>
                <label className="text-xs font-bold text-gray-400 block mb-1">Tên đầy đủ nhân sự</label>
                <input required type="text" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} className="w-full border border-gray-200 rounded-xl px-4 py-2.5 outline-none focus:border-blue-500 text-sm bg-gray-50" placeholder="Nguyễn Văn A" />
              </div>
              <div>
                <label className="text-xs font-bold text-gray-400 block mb-1">Địa chỉ Email</label>
                <input required type="email" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} className="w-full border border-gray-200 rounded-xl px-4 py-2.5 outline-none focus:border-blue-500 text-sm bg-gray-50" placeholder="doctor@clinic.com" />
              </div>
              <div>
                <label className="text-xs font-bold text-gray-400 block mb-1">Mật khẩu</label>
                <input required type="password" value={formData.password} onChange={e => setFormData({...formData, password: e.target.value})} className="w-full border border-gray-200 rounded-xl px-4 py-2.5 outline-none focus:border-blue-500 text-sm bg-gray-50" placeholder="••••••••" />
              </div>
              <div>
                <label className="text-xs font-bold text-gray-400 block mb-1">Chức vụ</label>
                <select value={formData.role} onChange={e => setFormData({...formData, role: e.target.value})} className="w-full border border-gray-200 rounded-xl px-4 py-2.5 outline-none focus:border-blue-500 text-sm bg-gray-50 font-semibold text-gray-600">
                  <option value="DOCTOR">DOCTOR (Bác sĩ chuyên khoa)</option>
                  <option value="ADMIN">ADMIN (Quản trị cơ sở)</option>
                </select>
              </div>
            </div>

            <div className="flex gap-3 pt-5 mt-6 border-t border-gray-100">
              <button type="button" onClick={() => setIsOpenModal(false)} className="flex-1 py-2.5 rounded-xl text-xs font-bold text-gray-500 hover:bg-gray-100">Hủy</button>
              <button type="submit" className="flex-1 py-2.5 rounded-xl text-xs font-bold bg-blue-600 hover:bg-blue-700 text-white shadow-md">Lưu thông tin</button>
            </div>
          </form>
        </div>
      )}

      {/* POPUP: DANH SÁCH GÓI DỊCH VỤ GIA HẠN / NÂNG CẤP [FR-28] */}
      {isOpenUpgradeModal && (
        <div className="fixed inset-0 bg-gray-900/50 backdrop-blur-sm flex justify-center items-start z-50 p-4 overflow-y-auto">
          <div className="bg-white rounded-3xl max-w-5xl w-full p-6 shadow-2xl border border-gray-100 my-8">
            
            {/* Thanh Tiêu đề và nút tắt modal */}
            <div className="flex justify-between items-center pb-4 border-b border-gray-100 mb-6">
              <div>
                <h3 className="text-lg font-black text-gray-800">Danh mục Gói Dịch Vụ Hệ Thống</h3>
                <p className="text-xs text-gray-400 font-medium mt-0.5">Chọn nâng cấp hoặc gia hạn chu kỳ sử dụng cho cơ sở của bạn</p>
              </div>
              <button 
                type="button" 
                onClick={() => setIsOpenUpgradeModal(false)} 
                className="text-gray-400 hover:text-gray-600 text-2xl font-bold p-2 transition-colors">
                &times;
              </button>
            </div>

            {/* Bố cục lưới chia 3 cột ngang (Responsive: Tự động xếp dọc trên màn hình điện thoại) */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 my-2">
              {packages.map((pkg) => (
                <div 
                  key={pkg.id} 
                  className={`border rounded-2xl p-5 flex flex-col justify-between hover:shadow-lg transition-all relative overflow-hidden bg-white ${
                    stats?.currentPackage?.id === pkg.id ? 'border-amber-500 ring-2 ring-amber-500/20' : 'border-gray-200'
                  }`}>
                  
                  {stats?.currentPackage?.id === pkg.id && (
                    <span className="absolute top-0 right-0 bg-amber-500 text-white text-[9px] font-black uppercase tracking-wider px-2.5 py-1 rounded-bl-xl">
                      Đang dùng
                    </span>
                  )}

                  <div>
                    <h4 className="text-base font-black text-slate-900 mb-1">{pkg.name}</h4>
                    <p className="text-[11px] text-gray-400 min-h-[48px] leading-relaxed">
                      {pkg.description || "Tối ưu hóa quy trình khám và phân tích ảnh với AI Core."}
                    </p>
                    
                    <div className="my-4 bg-slate-50 rounded-xl p-3 border border-slate-100">
                      <span className="text-2xl font-black text-emerald-600">
                        {Number(pkg.price).toLocaleString('vi-VN')}
                      </span>
                      <span className="text-xs text-gray-400 font-bold"> VNĐ</span>
                    </div>

                    <ul className="text-xs text-gray-600 space-y-2 mb-6 font-medium">
                      <li className="flex items-center gap-1.5">🔹 Hạn mức: <strong className="text-slate-800">{pkg.imageLimit} lượt ảnh</strong></li>
                      <li className="flex items-center gap-1.5">🔹 Chu kỳ: <strong className="text-slate-800">{pkg.durationDays} ngày</strong></li>
                    </ul>
                  </div>

                  <button 
                    type="button"
                    disabled={stats?.currentPackage?.id === pkg.id}
                    onClick={() => handleUpgradePackage(pkg.id, pkg.name)}
                    className={`w-full font-bold text-xs py-2.5 rounded-xl transition-all shadow-sm ${
                      stats?.currentPackage?.id === pkg.id 
                        ? 'bg-slate-100 text-slate-400 cursor-not-allowed shadow-none' 
                        : 'bg-slate-900 hover:bg-blue-600 text-white'
                    }`}>
                    {stats?.currentPackage?.id === pkg.id ? 'Gói hiện tại' : 'Mua ngay'}
                  </button>

                </div>
              ))}
            </div>

          </div>
        </div>
      )}
    </div>
  );
};

export default ClinicDashboard;