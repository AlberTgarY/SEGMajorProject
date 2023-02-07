import BackendService from './backend-service'

export default {
  async uploadImage(fileName) {
    let formData = new FormData();
    formData.append("file", fileName);
    const res = await BackendService.uploadImage(formData)
    return res;
  }
}