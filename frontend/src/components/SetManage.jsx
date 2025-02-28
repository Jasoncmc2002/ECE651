import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import ContendCard from './contents/ContentCard';
import Brand from '../images/icon-with-name.png';
import { House, Stopwatch } from 'react-bootstrap-icons';

class SetManage extends Component {
    state = {}

    handlePermissionRender = () => {
        if (this.props.permission > 0) {
            return (
                <div className="container-fluid">
                    <div className="row">
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/set_manage/assignment/">
                                    Assignments
                                    <House />
                                </Link>
                            </h4>
                        </div>
                        <div className="col">
                            <h4 className="text-center">
                                <Link className='icon-link icon-link-hover' to="/set_manage/exam/">
                                    Exams
                                    <Stopwatch />
                                </Link>
                            </h4>
                        </div>
                    </div>
                </div>
            );
        } else {
            return (
                <h4 className="text-center" style={{ textDecoration: "none" }}>You do not have permission to access this page</h4>
            );
        }
    }

    handleAccountRender = () => {
        if (this.props.is_login) {
            let now = new Date().getHours();
            let greetings = now < 12 ? "Good morning" : now < 18 ? "Good afternoon" : "Good evening";
            // let tag = this.props.permission < 1 ? "同学" : this.props.permission < 2 ? "老师" : "管理员";

            return (
                <React.Fragment>
                    <div className="d-flex justify-content-center mb-3">
                        <img src={Brand} className='img-fluid' alt="Brand" style={{ maxWidth: "100%" }}></img>
                    </div>
                    <h4 className="text-center mb-3">{greetings}, {this.props.name}</h4>
                    <hr />
                    {this.handlePermissionRender()}
                </React.Fragment>
            );
        } else {
            return (
                <React.Fragment>
                    <h1 className="text-center">Set Manage</h1>
                    <hr />
                    <h4 className="text-center">Please <Link className='btn btn-link px-0' to="/login/" style={{ textDecoration: "none" }}><h4 className='mb-1'>sign in</h4></Link> to access</h4>
                </React.Fragment>
            );
        }
    }

    render() {
        return (
            <div className="container">
                <div className="row justify-content-md-center align-items-center" style={{ height: "90vh" }}>
                    <div className="col col-md-7">
                        <div className="container-fluid">
                            <ContendCard>
                                {this.handleAccountRender()}
                            </ContendCard>
                        </div>
                    </div>
                </div>
            </div>
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

export default connect(mapStateToProps, null)(SetManage);