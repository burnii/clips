import { Component, Vue, Inject } from 'vue-property-decorator';

import { IClipUser } from '@/shared/model/clip-user.model';
import ClipUserService from './clip-user.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class ClipUserDetails extends Vue {
  @Inject('clipUserService') private clipUserService: () => ClipUserService;
  @Inject('alertService') private alertService: () => AlertService;

  public clipUser: IClipUser = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.clipUserId) {
        vm.retrieveClipUser(to.params.clipUserId);
      }
    });
  }

  public retrieveClipUser(clipUserId) {
    this.clipUserService()
      .find(clipUserId)
      .then(res => {
        this.clipUser = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
