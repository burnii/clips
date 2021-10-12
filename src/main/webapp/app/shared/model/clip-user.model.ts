import { IUser } from '@/shared/model/user.model';
import { IClip } from '@/shared/model/clip.model';

export interface IClipUser {
  id?: number;
  internalUser?: IUser | null;
  clips?: IClip[] | null;
  ratedClips?: IClip[] | null;
}

export class ClipUser implements IClipUser {
  constructor(public id?: number, public internalUser?: IUser | null, public clips?: IClip[] | null, public ratedClips?: IClip[] | null) {}
}
