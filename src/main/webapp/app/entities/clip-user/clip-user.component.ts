import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IClipUser } from '@/shared/model/clip-user.model';

import ClipUserService from './clip-user.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class ClipUser extends Vue {
  @Inject('clipUserService') private clipUserService: () => ClipUserService;
  @Inject('alertService') private alertService: () => AlertService;

  private removeId: number = null;

  public clipUsers: IClipUser[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllClipUsers();
  }

  public clear(): void {
    this.retrieveAllClipUsers();
  }

  public retrieveAllClipUsers(): void {
    this.isFetching = true;
    this.clipUserService()
      .retrieve()
      .then(
        res => {
          this.clipUsers = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: IClipUser): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeClipUser(): void {
    this.clipUserService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('clipsApp.clipUser.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllClipUsers();
        this.closeDialog();
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
