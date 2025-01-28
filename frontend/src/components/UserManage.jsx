import React, { Component } from 'react';
import { connect } from 'react-redux';
import ContendCard from './contents/ContentCard';
import { Link } from 'react-router-dom';
import $ from 'jquery';
import BACKEND_ADDRESS_URL from './config/BackendAddressURLConfig';

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

class UserManage extends Component {
    state = {
        target_user_id: '',
        target_username: '',
        target_name: '',
        target_permission: -1,

        target_password: '',
        target_password_confirm: '',

        has_target: false,
        has_target_change: false,
        has_password_change: false,

        username_search: '',
        name_search: '',
        searched_user_list: [
            // { user_id: '4', name: 'bb', username: '04', permission: '0' },
            // { user_id: '5', name: 'cc', username: '05', permission: '1' },
            // { user_id: '6', name: 'dd', username: '06', permission: '2' },
        ],

        has_deleted: true,

        batch_create_user_list: [
            // { name: 'bb', username: '04', permission: '0', password: 'test123', error_message: 'Password length cannot be greater than 100' },
            // { name: 'cc', username: '05', permission: '1', password: 'test123', error_message: '用户名长度不能大于100' },
            // { name: 'dd', username: '06', permission: '2', password: 'test123', error_message: 'success' },
        ],
        batch_create_success_count: "--",
        batch_create_error_count: "--",
        batch_loaded: false,  // load file to be true, submit to be false

        is_loading: false,
        error_message: '',
    }

    handleSubmitTargetChange = () => {
        // console.log("target change");
        // console.log(this.state);
        // 检查输入
        this.setState({
            error_message: "",
            is_loading: true,
        });

        if (this.state.target_username === "") {
            this.setState({
                error_message: "Username cannot be empty",
                is_loading: false,
            });
        } else if (this.state.target_name === "") {
            this.setState({
                error_message: "Name cannot be empty",
                is_loading: false,
            });
        } else if (parseInt(this.state.target_permission) < 0 || parseInt(this.state.target_permission) > 2) {
            this.setState({
                error_message: "Invalid permission value",
                is_loading: false,
            });
        } else {
            const token = this.props.token;
            // console.log(token);
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/admin/user_info/",
                type: "PUT",
                data: {
                    userId: this.state.target_user_id,
                    username: this.state.target_username,
                    name: this.state.target_name,
                    permission: this.state.target_permission
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === 'success') {
                        this.setState({
                            is_loading: false,
                            has_target_change: false,
                            error_message: "User information updated successfully",
                        });
                    } else {
                        this.setState({
                            is_loading: false,
                            error_message: resp.error_message,
                        });
                    }
                }
            });
        }
    }

    handleSubmitPasswordChange = () => {
        // console.log("password change");
        // console.log(this.state);
        this.setState({
            error_message: "",
            is_loading: true,
        });
        // 检查输入
        if (this.state.target_password === "") {
            this.setState({
                error_message: "Password cannot be empty",
                is_loading: false,
            })
        } else if (this.state.target_password_confirm === "") {
            this.setState({
                error_message: "Confirm password cannot be empty",
                is_loading: false,
            })
        } else if (this.state.target_password !== this.state.target_password_confirm) {
            this.setState({
                error_message: "The passwords you entered twice do not match",
                is_loading: false,
            })
        } else {
            const token = this.props.token;
            // console.log(token);
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/admin/password/",
                type: "PUT",
                data: {
                    userId: this.state.target_user_id,
                    password: this.state.target_password,
                    confirmedPassword: this.state.target_password_confirm,
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === 'success') {
                        this.setState({
                            has_password_change: false,
                            is_loading: false,
                            error_message: "Password updated successfully",
                            target_password: '',
                            target_password_confirm: '',
                        });
                    } else {
                        this.setState({
                            is_loading: false,
                            error_message: resp.error_message,
                        });
                    }
                }
            });
        }
    }

    handleUserSearch = () => {
        // console.log("user search");
        // console.log(this.state);
        const token = this.props.token;
        // console.log(token);
        this.setState({
            is_loading: true,
        });
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/user/account/admin/search/",
            type: "GET",
            data: {
                username: this.state.username_search,
                name: this.state.name_search
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                // console.log(resp);
                this.setState({
                    searched_user_list: resp,
                    is_loading: false,
                });
                // 此处不会特判error message
            }
        });
    }

    handleLoadTarget = (user) => {
        // remove that user from the list
        const index = this.state.searched_user_list.indexOf(user);
        const new_searched_user_list = this.state.searched_user_list.toSpliced(index, 1);
        // copy information
        this.setState({
            target_user_id: user.user_id,
            target_username: user.username,
            target_name: user.name,
            target_permission: user.permission,
            has_target: true,
            searched_user_list: new_searched_user_list,
        });
    }

    handleDeleteTarget = (user) => {
        // Remember to remove that from the original list
        // console.log("delete");
        // console.log(user);
        this.setState({
            error_message: '',
            is_loading: true,
        });
        const token = this.props.token;
        // console.log(user.user_id);
        $.ajax({
            url: BACKEND_ADDRESS_URL + "/user/account/admin/delete/",
            type: "DELETE",
            data: {
                userId: user.user_id,
            },
            headers: {
                Authorization: "Bearer " + token
            },
            success: (resp) => {
                if (resp.error_message === 'success') {
                    const index = this.state.searched_user_list.indexOf(user);
                    const new_searched_user_list = this.state.searched_user_list.toSpliced(index, 1);
                    this.setState({
                        has_deleted: true,
                        searched_user_list: new_searched_user_list,
                        is_loading: false,
                        error_message: "User deleted successfully"
                    });
                } else {
                    this.setState({
                        is_loading: false,
                        error_message: resp.error_message,
                    });
                }
            }
        });

    }

    handleTitleLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border ms-2" aria-hidden="true"></span>
            );
        }
    }

    handleButtonLoadingRender = () => {
        if (this.state.is_loading) {
            return (
                <span className="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
            );
        }
    }

    handleDeleteModalRender = (user) => {
        const cannot_modify = parseInt(user.user_id) === this.props.user_id;
        return (
            <React.Fragment>
                {/* < !--Button trigger modal-- > */}
                <button type="button" className='btn btn-sm btn-outline-danger ms-2' disabled={this.state.is_loading || cannot_modify} data-bs-toggle="modal" data-bs-target={"#deleteModal" + user.user_id} onClick={() => {
                    this.setState({
                        has_deleted: false,
                        error_message: '',
                    });
                }}>Delete</button>

                {/* <!-- Modal-- > */}
                <div className="modal fade" id={"deleteModal" + user.user_id} tabIndex="-1" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="deleteModalLabel">Confirm Delete</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                The deletion operation is irreversible. Do you confirm to delete the user?
                            </div>
                            <div className="modal-footer">
                                <span className='text-md-end' style={{ color: "red" }}>
                                    {this.state.error_message}
                                </span>
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <button type="button" className="btn btn-danger" disabled={this.state.is_loading || this.state.has_deleted} data-bs-dismiss="modal" onClick={() => this.handleDeleteTarget(user)}>Confirm Delete</button>
                            </div>
                        </div>
                    </div>
                </div>
            </React.Fragment>
        );
    }

    handleTargetRender = () => {
        return (
            <div className="container-fluid">
                <ContendCard>
                    <div className="row">
                        <div className="col">
                            <h5>Update User Information</h5>
                        </div>
                        <div className="col-auto">
                            <button className='btn btn-secondary' disabled={!this.state.has_target || this.state.is_loading} onClick={() => {
                                this.setState({
                                    target_user_id: '',
                                    target_username: '',
                                    target_name: '',
                                    target_permission: '',

                                    target_password: '',
                                    target_password_confirm: '',

                                    has_target: false,
                                    has_target_change: false,
                                    has_password_change: false,
                                });
                            }}>
                                {/* {this.handleButtonLoadingRender()} */}
                                Clear
                            </button>
                        </div>
                        <div className="col-auto">
                            <button className='btn btn-success' disabled={!this.state.has_target || !this.state.has_target_change || this.state.is_loading} onClick={() => this.handleSubmitTargetChange()}>
                                {/* {this.handleButtonLoadingRender()} */}
                                Update Information
                            </button>
                        </div>
                    </div>
                    <div className="row mt-2">
                        <div className="col-2">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="targetUserId" className="col-form-label">ID</label>
                                </div>
                                <div className="col">
                                    <input type="text" id="targetUserId" className="form-control" value={this.state.target_user_id} disabled />
                                </div>
                            </div>
                        </div>
                        <div className="col">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="targetUsername" className="col-form-label">Username</label>
                                </div>
                                <div className="col">
                                    <input type="text" id="targetUsername" className="form-control" value={this.state.target_username} disabled={!this.state.has_target || this.state.is_loading} onChange={(e) => {
                                        this.setState({
                                            target_username: e.target.value,
                                            has_target_change: true,
                                        });
                                    }} />
                                </div>
                            </div>
                        </div>
                        <div className="col">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="targetName" className="col-form-label">Name</label>
                                </div>
                                <div className="col">
                                    <input type="text" id="targetName" className="form-control" value={this.state.target_name} disabled={!this.state.has_target || this.state.is_loading} onChange={(e) => {
                                        this.setState({
                                            target_name: e.target.value,
                                            has_target_change: true,
                                        });
                                    }} />
                                </div>
                            </div>
                        </div>
                        <div className="col-3">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="targetPermission" className="col-form-label">Permission</label>
                                </div>
                                <div className="col">
                                    <select className="form-select" id='targetPermission' aria-label="Default select example" value={this.state.target_permission} disabled={!this.state.has_target || this.state.is_loading} onChange={(e) => {
                                        if (e.target.value !== '-1') {
                                            this.setState({
                                                target_permission: e.target.value,
                                                has_target_change: true,
                                            });
                                        } else {
                                            this.setState({
                                                target_permission: e.target.value,
                                                has_target_change: false,
                                            });
                                        }
                                    }}>
                                        <option value={'-1'}>--</option>
                                        <option value="0">Student</option>
                                        <option value="1">Teacher</option>
                                        <option value="2">Administrator</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>

                    <hr />
                    <div className="row">
                        <div className="col">
                            <h5>Change User Password</h5>
                        </div>
                        <div className="col-auto">
                            <button className='btn btn-primary' disabled={this.state.is_loading || !this.state.has_password_change} onClick={() => this.handleSubmitPasswordChange()}>
                                {/* {this.handleButtonLoadingRender()} */}
                                Change Password
                            </button>
                        </div>
                    </div>

                    <div className="row mt-2">
                        <div className="col">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor='password' className='col-form-label'>Password</label>
                                </div>
                                <div className="col">
                                    <input type="password" id="password" className="form-control" disabled={!this.state.has_target || this.state.is_loading} onChange={(e) => {
                                        this.setState({
                                            target_password: e.target.value,
                                            has_password_change: true
                                        });
                                    }} />
                                </div>
                            </div>
                        </div>
                        <div className="col">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor='passwordConfirm' className='col-form-label'>Confirm Password</label>
                                </div>
                                <div className="col">
                                    <input type="password" id="passwordConfirm" className="form-control" disabled={!this.state.has_target || this.state.is_loading} onChange={(e) => {
                                        this.setState({
                                            target_password_confirm: e.target.value,
                                            has_password_change: true
                                        });
                                    }} />
                                </div>
                            </div>
                        </div>
                    </div>
                </ContendCard>
            </div>
        );
    }

    handleSearchRender = () => {
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h5>User Search</h5>
                    {/* search box */}
                    <div className="row">
                        <div className="col">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="usernameSearch" className="col-form-label">Username</label>
                                </div>
                                <div className="col">
                                    <input type="search" className="form-control" id="usernameSearch"
                                        value={this.state.username_search}
                                        onChange={(e) => {
                                            this.setState({ username_search: e.target.value });
                                        }}
                                        disabled={this.state.is_loading}
                                        placeholder='Support fuzzy search' />
                                </div>
                            </div>
                        </div>
                        <div className="col">
                            <div className="row">
                                <div className="col-auto">
                                    <label htmlFor="nameSearch" className="col-form-label">Name</label>
                                </div>
                                <div className="col">
                                    <input type="search" className="form-control" id="nameSearch"
                                        value={this.state.name_search}
                                        onChange={(e) => {
                                            this.setState({ name_search: e.target.value });
                                        }}
                                        disabled={this.state.is_loading}
                                        placeholder='Support fuzzy search' />
                                </div>
                            </div>
                        </div>
                        <div className="col-auto">
                            <div className="row">
                                <div className="col">
                                    <button className='btn btn-success' disabled={this.state.is_loading} onClick={() => this.handleUserSearch()}>Search</button>
                                </div>
                                {/* <div className="col">
                                    <button className='btn btn-secondary' disabled={this.state.is_loading} onClick={() => {
                                        this.setState({
                                            username_search: '',
                                            name_search: '',
                                        });
                                    }}>清除</button>
                                </div> */}
                            </div>
                        </div>
                    </div>
                    {/* search result table */}

                    <table className='table table-hover'>
                        <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Username</th>
                                <th scope="col">Name</th>
                                <th scope="col">Permission</th>
                                <th scope="col">Operation</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.searched_user_list.map((user) => {
                                if (!user.user_id) {
                                    return;
                                }
                                const permission = parseInt(user.permission);
                                const permission_str = permission < 1 ? "Student" : permission < 2 ? "Teacher" : "Administrator";
                                const style = {};
                                const cannot_modify = parseInt(user.user_id) === this.props.user_id;
                                return (
                                    <tr key={"user" + user.user_id}>
                                        <th className='align-middle' scope='col' style={{ width: "48px" }}>{user.user_id}</th>
                                        <td className='align-middle' style={style}>
                                            {user.username}
                                        </td>
                                        <td className='align-middle' style={style}>
                                            {user.name}
                                        </td>
                                        <td className='align-middle' style={{ width: "64px" }}>{permission_str}</td>
                                        <td className='align-middle' style={{ width: "150px" }}>
                                            <button className='btn btn-sm btn-outline-success' onClick={() => this.handleLoadTarget(user)} disabled={this.state.is_loading || cannot_modify}>Update</button>
                                            {this.handleDeleteModalRender(user)}
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </ContendCard>
            </div>
        );
    }

    cautionModalRender = () => {
        return (
            <React.Fragment>
                <button type="button" className="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#cautionModal">
                    Download Template
                </button>

                <div className="modal fade" id="cautionModal" tabIndex="-1" aria-labelledby="cautionModalLabel" aria-hidden="true">
                    <div className="modal-dialog modal-dialog-centered modal-lg">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5" id="cautionModalLabel">Download Template</h1>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <p><b>Please use the downloaded template to fill in the user information. Do not modify the template column names. </b>The column names and meanings are as follows:</p>

                                <table className="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th scope="col">Column</th>
                                            <th scope="col">Meaning</th>
                                            <th scope="col">Requirement</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td><code>username</code></td>
                                            <td>Username</td>
                                            <td>Required, unique, no more than 100 characters</td>
                                        </tr>
                                        <tr>
                                            <td><code>name</code></td>
                                            <td>Name</td>
                                            <td>Required, no more than 100 characters</td>
                                        </tr>
                                        <tr>
                                            <td><code>password</code></td>
                                            <td>Password</td>
                                            <td>Required, no more than 100 characters</td>
                                        </tr>
                                        <tr>
                                            <td><code>permission</code></td>
                                            <td>Permission</td>
                                            <td>Required, 0 for student, 1 for teacher, 2 for administrator, do not enter other values</td>
                                        </tr>
                                    </tbody>
                                </table>

                                <p>After importing data, you should first check whether the data is imported correctly, and then click "Submit" to complete the creating process. After the creation is completed, please check whether there are any errors, and re-operate the creation if there are any errors.</p>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                {/* <button type="button" className="btn btn-primary">Save changes</button> */}
                                <button className='btn btn-outline-primary ms-2' onClick={() => this.handleTemplateDownload()}>Download</button>
                            </div>
                        </div>
                    </div>
                </div>
            </React.Fragment>
        );
    }

    handleTemplateDownload = () => {
        // console.log("download");
        const data = [
            { "username": "", "name": "", "password": "", "permission": "" }
        ];

        const worksheet = XLSX.utils.json_to_sheet(data);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Import User");
        // Buffer to store the generated Excel file
        const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
        const blob = new Blob([excelBuffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8' });

        saveAs(blob, "Import User.xlsx");
    }

    handleFileInput = (e) => {
        const file = e.target.files[0];
        // https://developer.mozilla.org/en-US/docs/Web/API/File
        // console.log(file.name);
        const reader = new FileReader();
        // https://developer.mozilla.org/en-US/docs/Web/API/FileReader

        reader.addEventListener("load", (e) => {
            const workbook = XLSX.read(e.target.result, { type: 'binary' });
            const sheetName = workbook.SheetNames[0];
            const sheet = workbook.Sheets[sheetName];
            const sheetData = XLSX.utils.sheet_to_json(sheet);

            const batch_create_user_list = [];
            sheetData.forEach((row) => {
                batch_create_user_list.push({
                    ...row,
                    error_message: "Waiting for upload"
                })
            })

            // console.log(sheetData);
            // console.log(batch_create_user_list);
            this.setState({
                batch_create_user_list: batch_create_user_list,
                batch_loaded: true,
                batch_create_error_count: 0,
                batch_create_success_count: 0,
            });
        })

        if (file) {
            reader.readAsArrayBuffer(file);
        }
    }

    handleSubmitBatchCreate = () => {
        // console.log("batch create submit");
        const token = this.props.token;
        // console.log(token);
        this.setState({
            error_message: '',
            is_loading: true
        });
        const batch_create_user_list_result = [];
        let batch_create_success_count = 0, batch_create_error_count = 0;
        this.state.batch_create_user_list.forEach((user) => {
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/admin/batch_create/",
                type: "POST",
                data: {
                    username: user.username,
                    name: user.name,
                    password: user.password,
                    permission: user.permission
                },
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    // console.log(resp);
                    if (resp.error_message === 'success') {
                        batch_create_user_list_result.push({
                            ...user,
                            error_message: "Create successfully"
                        });
                        batch_create_success_count += 1;
                    } else {
                        batch_create_user_list_result.push({
                            ...user,
                            error_message: resp.error_message
                        });
                        batch_create_error_count += 1;
                    }
                },
                error: () => {
                    batch_create_user_list_result.push({
                        ...user,
                        error_message: "Network connection or internal server error"
                    });
                    batch_create_error_count += 1;
                },
                async: false  // ajax is asynchronous, set this to prevent weird behavior
            });
        });
        // console.log(batch_create_user_list_result);
        this.setState({
            is_loading: false,
            batch_create_user_list: batch_create_user_list_result,
            batch_loaded: false,
            batch_create_error_count: batch_create_error_count,
            batch_create_success_count: batch_create_success_count,
        });
    }

    batchAddRender = () => {
        return (
            <div className="container-fluid">
                <ContendCard>
                    <h5>Import User</h5>

                    <div className="row align-items-center">
                        <div className="col-7">
                            <div className="row justify-content-around">
                                <div className="col-auto">
                                    Total: <strong>{this.state.batch_create_user_list.length}</strong>
                                </div>
                                <div className="col-auto">
                                    Success: <strong>{this.state.batch_create_success_count}</strong>
                                </div>
                                <div className="col-auto">
                                    Error: <strong>{this.state.batch_create_error_count}</strong>
                                </div>
                            </div>
                        </div>
                        <div className="col-5">
                            <div className="row justify-content-between">
                                <div className="col-auto">
                                    {this.cautionModalRender()}
                                </div>
                                <div className="col-auto">
                                    <label htmlFor='batchImport' className='btn btn-outline-primary'>Import Data</label>
                                    <input type='file' accept='.xlsx' className='form-control' id='batchImport' hidden onChange={(e) => this.handleFileInput(e)} />
                                </div>
                                <div className="col-auto">
                                    <button className='btn btn-success' disabled={!this.state.batch_loaded || this.state.is_loading} onClick={() => this.handleSubmitBatchCreate()}>Submit</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <table className='table table-hover'>
                        <thead>
                            <tr>
                                <th scope="col">Username</th>
                                <th scope="col">Name</th>
                                <th scope="col">Permission</th>
                                <th scope="col">Password</th>
                                <th scope="col">Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.batch_create_user_list.map((user) => {
                                const permission = parseInt(user.permission);
                                const permission_str = "permission" in user ? permission === 0 ? "Student" : permission === 1 ? "Teacher" : permission === 2 ? "Administrator" : "--" : "--";
                                const index = this.state.batch_create_user_list.indexOf(user);
                                return (
                                    <tr key={"batch_add_user" + index}>
                                        <td className='align-middle' style={{ width: "140px", wordBreak: "break-word", wordWrap: "break-word" }}>
                                            {user.username}
                                        </td>
                                        <td className='align-middle text-break' style={{ width: "240px" }}>
                                            {user.name}
                                        </td>
                                        <td className='align-middle text-break' style={{ width: "120px" }}>{permission_str}</td>
                                        <td className='align-middle' style={{ width: "300px" }}>
                                            <code>{user.password}</code>
                                        </td>
                                        <td className='align-middle' style={{ width: "390px" }}>
                                            <code>{user.error_message}</code>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </ContendCard>
            </div>
        );
    }

    handlePermissionRender = () => {
        if (this.props.permission > 1) {
            return (
                <div className="container">
                    <ContendCard>
                        <div className="row">
                            <div className="col">
                                <h4>User Management</h4>
                            </div>
                            <div className="col-auto text-end d-flex align-items-center">
                                <span style={{ color: "red" }}>{this.state.error_message}</span>
                                {this.handleTitleLoadingRender()}
                            </div>
                        </div>

                        {/* <hr /> */}

                        {/* target */}
                        {this.handleTargetRender()}

                        {/* search */}
                        {this.handleSearchRender()}

                        {/* batch add */}
                        {this.batchAddRender()}
                    </ContendCard>
                </div>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">User Management</h1>
                                    <hr />
                                    <h4 className="text-center">You do not have permission to view this page</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            );
        }
    }

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <React.Fragment>
                    {this.handlePermissionRender()}
                </React.Fragment>
            );
        } else {
            return (
                <div className="container">
                    <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                        <div className="col col-md-7">
                            <ContendCard>
                                <div className="row justify-content-md-center">
                                    <h1 className="text-center">User Management</h1>
                                    <hr />
                                    <h4 className="text-center">Please <Link className='btn btn-link px-0' to="/login/" style={{ textDecoration: "none" }}><h4 className='mb-1'>sign in</h4></Link> to access</h4>
                                </div>
                            </ContendCard>
                        </div>
                    </div>
                </div>
            );
        }
    }

    render() {
        return (
            <React.Fragment>
                {this.handleAccountRender()}
            </React.Fragment>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

export default connect(mapStateToProps, null)(UserManage);