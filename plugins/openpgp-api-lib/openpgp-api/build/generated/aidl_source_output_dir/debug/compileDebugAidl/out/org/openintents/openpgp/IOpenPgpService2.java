/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package org.openintents.openpgp;
public interface IOpenPgpService2 extends android.os.IInterface
{
  /** Default implementation for IOpenPgpService2. */
  public static class Default implements org.openintents.openpgp.IOpenPgpService2
  {
    /**
         * see org.openintents.openpgp.util.OpenPgpApi for documentation
         */
    @Override public android.os.ParcelFileDescriptor createOutputPipe(int pipeId) throws android.os.RemoteException
    {
      return null;
    }
    /**
         * see org.openintents.openpgp.util.OpenPgpApi for documentation
         */
    @Override public android.content.Intent execute(android.content.Intent data, android.os.ParcelFileDescriptor input, int pipeId) throws android.os.RemoteException
    {
      return null;
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements org.openintents.openpgp.IOpenPgpService2
  {
    private static final java.lang.String DESCRIPTOR = "org.openintents.openpgp.IOpenPgpService2";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an org.openintents.openpgp.IOpenPgpService2 interface,
     * generating a proxy if needed.
     */
    public static org.openintents.openpgp.IOpenPgpService2 asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof org.openintents.openpgp.IOpenPgpService2))) {
        return ((org.openintents.openpgp.IOpenPgpService2)iin);
      }
      return new org.openintents.openpgp.IOpenPgpService2.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_createOutputPipe:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          android.os.ParcelFileDescriptor _result = this.createOutputPipe(_arg0);
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        case TRANSACTION_execute:
        {
          data.enforceInterface(descriptor);
          android.content.Intent _arg0;
          if ((0!=data.readInt())) {
            _arg0 = android.content.Intent.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          android.os.ParcelFileDescriptor _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          int _arg2;
          _arg2 = data.readInt();
          android.content.Intent _result = this.execute(_arg0, _arg1, _arg2);
          reply.writeNoException();
          if ((_result!=null)) {
            reply.writeInt(1);
            _result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
          }
          else {
            reply.writeInt(0);
          }
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements org.openintents.openpgp.IOpenPgpService2
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      /**
           * see org.openintents.openpgp.util.OpenPgpApi for documentation
           */
      @Override public android.os.ParcelFileDescriptor createOutputPipe(int pipeId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.os.ParcelFileDescriptor _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(pipeId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_createOutputPipe, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().createOutputPipe(pipeId);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = android.os.ParcelFileDescriptor.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      /**
           * see org.openintents.openpgp.util.OpenPgpApi for documentation
           */
      @Override public android.content.Intent execute(android.content.Intent data, android.os.ParcelFileDescriptor input, int pipeId) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        android.content.Intent _result;
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((data!=null)) {
            _data.writeInt(1);
            data.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          if ((input!=null)) {
            _data.writeInt(1);
            input.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          _data.writeInt(pipeId);
          boolean _status = mRemote.transact(Stub.TRANSACTION_execute, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            return getDefaultImpl().execute(data, input, pipeId);
          }
          _reply.readException();
          if ((0!=_reply.readInt())) {
            _result = android.content.Intent.CREATOR.createFromParcel(_reply);
          }
          else {
            _result = null;
          }
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
        return _result;
      }
      public static org.openintents.openpgp.IOpenPgpService2 sDefaultImpl;
    }
    static final int TRANSACTION_createOutputPipe = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_execute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(org.openintents.openpgp.IOpenPgpService2 impl) {
      if (Stub.Proxy.sDefaultImpl == null && impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static org.openintents.openpgp.IOpenPgpService2 getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  /**
       * see org.openintents.openpgp.util.OpenPgpApi for documentation
       */
  public android.os.ParcelFileDescriptor createOutputPipe(int pipeId) throws android.os.RemoteException;
  /**
       * see org.openintents.openpgp.util.OpenPgpApi for documentation
       */
  public android.content.Intent execute(android.content.Intent data, android.os.ParcelFileDescriptor input, int pipeId) throws android.os.RemoteException;
}
