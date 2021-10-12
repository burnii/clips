import { Component, Vue, Inject } from 'vue-property-decorator';

import AlertService from '@/shared/alert/alert.service';

import UserService from '@/admin/user-management/user-management.service';

import ClipService from '@/entities/clip/clip.service';
import { IClip } from '@/shared/model/clip.model';

import { IClipUser, ClipUser } from '@/shared/model/clip-user.model';
import ClipUserService from './clip-user.service';

const validations: any = {
  clipUser: {},
};

@Component({
  validations,
})
export default class ClipUserUpdate extends Vue {
  @Inject('clipUserService') private clipUserService: () => ClipUserService;
  @Inject('alertService') private alertService: () => AlertService;

  public clipUser: IClipUser = new ClipUser();

  @Inject('userService') private userService: () => UserService;

  public users: Array<any> = [];

  @Inject('clipService') private clipService: () => ClipService;

  public clips: IClip[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.clipUserId) {
        vm.retrieveClipUser(to.params.clipUserId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
    this.clipUser.ratedClips = [];
  }

  public save(): void {
    this.isSaving = true;
    if (this.clipUser.id) {
      console.log(this.clipUser);
      this.clipUserService()
        .update(this.clipUser)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('clipsApp.clipUser.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.clipUserService()
        .create(this.clipUser)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('clipsApp.clipUser.created', { param: param.id });
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    }
  }

  public retrieveClipUser(clipUserId): void {
    this.clipUserService()
      .find(clipUserId)
      .then(res => {
        this.clipUser = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.userService()
      .retrieve()
      .then(res => {
        this.users = res.data;
      });
    this.clipService()
      .retrieve()
      .then(res => {
        this.clips = res.data;
      });
  }

  public getSelected(selectedVals, option): any {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
