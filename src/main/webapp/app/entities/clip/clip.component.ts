import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { IClip, Clip as ClipEntity } from '@/shared/model/clip.model';

import JhiDataUtils from '@/shared/data/data-utils.service';

import ClipService from './clip.service';
import AlertService from '@/shared/alert/alert.service';
import UserManagementService from '@/admin/user-management/user-management.service';
import { ClipUser } from '@/shared/model/clip-user.model';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class Clip extends mixins(JhiDataUtils) {
  @Inject('clipService') private clipService: () => ClipService;
  @Inject('alertService') private alertService: () => AlertService;
  @Inject('userService') private userManagementService: () => UserManagementService;

  private removeId: number = null;

  public clips: IClip[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllClips();
  }

  public clear(): void {
    this.retrieveAllClips();
  }

  public retrieveAllClips(): void {
    this.isFetching = true;
    this.clipService()
      .retrieve()
      .then(
        res => {
          console.log(res);
          this.clips = res.data;
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

  public prepareRemove(instance: IClip): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public takeScreenshot(): void {
    const takeScreenshot2 = async () => {
      const stream = await navigator.mediaDevices.getDisplayMedia();

      const track = stream.getVideoTracks()[0];

      const image = new ImageCapture(track);

      const bitmap = await image.grabFrame();

      track.stop();

      const canvas = <HTMLCanvasElement>document.createElement('CANVAS');
      const ctx = canvas.getContext('2d');
      canvas.height = bitmap.height;
      canvas.width = bitmap.width;
      ctx.drawImage(bitmap, 0, 0);
      const dataURL = canvas.toDataURL();
      const base64string = dataURL.split(',')[1];

      const clip: IClip = new ClipEntity();
      clip.name = new Date().toDateString();
      clip.negativeCount = 0;
      clip.positiveCount = 0;
      clip.content = base64string;
      clip.contentContentType = 'image/png';

      this.userManagementService()
        .getCurrentUser()
        .then(res => {
          clip.creator = res.data;
          new ClipService()
            .create(clip)
            .then(res => {
              this.retrieveAllClips();
            })
            .catch(err => {
              console.log(err);
            });
        });
    };

    takeScreenshot2();
  }

  rateUp(index: number) {
    this.clips[index].positiveCount++;
    this.userManagementService()
      .getCurrentUser()
      .then(res => {
        if (this.clips[index].ratedUsers == null) {
          this.clips[index].ratedUsers = [];
        }

        this.clips[index].ratedUsers.push(new ClipUser());
        console.log(this.clips[index]);
        this.clipService().update(this.clips[index]);
      });
  }

  rateDown(index: number) {
    this.clips[index].negativeCount++;
    this.clipService().update(this.clips[index]);
  }

  public removeClip(): void {
    this.clipService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('clipsApp.clip.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllClips();
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
