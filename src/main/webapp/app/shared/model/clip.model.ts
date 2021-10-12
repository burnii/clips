import { IClipUser } from '@/shared/model/clip-user.model';

export interface IClip {
  id?: number;
  name?: string;
  contentContentType?: string;
  content?: string;
  positiveCount?: number | null;
  negativeCount?: number | null;
  creator?: IClipUser | null;
  ratedUsers?: IClipUser[] | null;
}

export class Clip implements IClip {
  constructor(
    public id?: number,
    public name?: string,
    public contentContentType?: string,
    public content?: string,
    public positiveCount?: number | null,
    public negativeCount?: number | null,
    public creator?: IClipUser | null,
    public ratedUsers?: IClipUser[] | null
  ) {}
}
