import BackendService from './backend-service'

export default {
  medicinePromise: null,

  async getAllMedicines() {
    if (!this.medicinePromise) {
      this.medicinePromise = BackendService.getAllMedicines()
                                           .then(function(res) {
        return res.data;
      });
    }
    
    return await this.medicinePromise;
  },

  async createMedicine(data) {
    const res = await BackendService.createMedicine(data);
    this.medicinePromise = null;
    return res.data;
  },

  async updateMedicine(data) {
    const res = await BackendService.updateMedicine(data);
    this.medicinePromise = null;
    return res.data;
  },

  async deleteMedicine(data) {
    const res = await BackendService.deleteMedicine(data);
    this.medicinePromise = null;
    return res.data;
  },
}
